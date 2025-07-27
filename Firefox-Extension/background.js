// bookmark-locker-extension/background.js

// 1. Polyfill for browser API at the very top.
//    This ensures 'browser' (Firefox's WebExtensions API) or 'chrome' (Chrome's API)
//    is correctly referenced before any API calls are made.
if (typeof browser === 'undefined') {
  var browser = chrome;
}

// 2. Load Firebase SDKs
//    These are imported via CDN in popup.html, but for background scripts (Manifest V2),
//    it's necessary to explicitly import them using importScripts.
importScripts(
  'https://www.gstatic.com/firebasejs/10.12.2/firebase-app-compat.js',
  'https://www.gstatic.com/firebasejs/10.12.2/firebase-auth-compat.js',
  'https://www.gstatic.com/firebasejs/10.12.2/firebase-firestore-compat.js'
);

// 3. Firebase Web App configuration
const firebaseConfig = {
  apiKey: "AIzaSyB0N_gZtytJwLld1iD98OSUruBM58Ess4w",
  authDomain: "bookmarklocker-ed7c9.firebaseapp.com",
  projectId: "bookmarklocker-ed7c9",
  storageBucket: "bookmarklocker-ed7c9.appspot.com", // Corrected storageBucket if it was .firebasestorage.app
  messagingSenderId: "153472220440",
  appId: "1:153472220440:web:187adfda15c8e00c5c726d"
};

// 4. Initialize Firebase
const app = firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.firestore();
console.log("Firebase background script loaded and initialized.");

let firestoreListenerUnsubscribe = null; // To manage Firestore listener

// ---- Google Sign-In ----
async function signInWithGoogle() {
  console.log("Initiating Google Sign-In from background...");
  return new Promise((resolve, reject) => {
    browser.identity.getAuthToken({ interactive: true })
      .then(accessToken => {
        if (!accessToken) {
          console.error("No access token received.");
          return reject(new Error("No access token."));
        }
        console.log("Google Access Token obtained:", accessToken);
        const credential = firebase.auth.GoogleAuthProvider.credential(accessToken);
        return auth.signInWithCredential(credential);
      })
      .then(userCredential => {
        const user = userCredential.user;
        console.log("Signed in with Firebase:", user.uid, user.email);
        setupFirestoreListener(user.uid);
        resolve({ uid: user.uid, email: user.email });
      })
      .catch(error => {
        console.error("Google Sign-In failed:", error);
        reject(error);
      });
  });
}

// ---- Sign-out ----
async function signOutUser() {
  if (firestoreListenerUnsubscribe) {
    firestoreListenerUnsubscribe();
    firestoreListenerUnsubscribe = null;
    console.log("Firestore listener detached.");
  }
  await auth.signOut();
  console.log("User signed out.");
}

// ---- Save bookmark to Firestore ----
async function saveBookmarkToFirestore(bookmarkData) {
  const user = auth.currentUser;
  if (!user) {
    console.error("No user signed in to save bookmark.");
    throw new Error("User not signed in.");
  }
  const bookmarksCollectionRef = db.collection(`users/${user.uid}/bookmarks`);
  const newBookmarkRef = bookmarksCollectionRef.doc();
  const dataToSave = {
    ...bookmarkData,
    id: newBookmarkRef.id,
    addDate: firebase.firestore.FieldValue.serverTimestamp(),
    isFavorite: false,
    isReadingList: false,
    isDeleted: false,
    deletedTimestamp: null,
    source: 'extension'
  };
  await newBookmarkRef.set(dataToSave);
  console.log("Bookmark saved to Firestore:", dataToSave.title);
  return dataToSave;
}

// ---- Delete bookmark from Firestore (soft delete) ----
async function deleteBookmarkFromFirestore(bookmarkId) {
  const user = auth.currentUser;
  if (!user) {
    console.error("No user signed in to delete bookmark.");
    throw new Error("User not signed in.");
  }
  const bookmarkRef = db.collection(`users/${user.uid}/bookmarks`).doc(bookmarkId);
  await bookmarkRef.update({
    isDeleted: true,
    deletedTimestamp: firebase.firestore.FieldValue.serverTimestamp()
  });
  console.log("Bookmark soft-deleted in Firestore:", bookmarkId);
}

// ---- Firestore Listener, sync Firestore changes to browser bookmarks ----
function setupFirestoreListener(uid) {
  if (firestoreListenerUnsubscribe) {
    firestoreListenerUnsubscribe();
    firestoreListenerUnsubscribe = null;
  }
  const bookmarksCollectionRef = db.collection(`users/${uid}/bookmarks`);
  firestoreListenerUnsubscribe = bookmarksCollectionRef.onSnapshot((snapshot) => {
    snapshot.docChanges().forEach((change) => {
      const bookmark = change.doc.data();
      const firestoreId = bookmark.id;
      let browserBookmarkId = bookmark.browserBookmarkId;
      if (change.type === "added") {
        if (!bookmark.isDeleted) {
          browser.bookmarks.search({ url: bookmark.url }).then(existingBookmarks => {
            if (existingBookmarks.length === 0) {
              browser.bookmarks.create({
                parentId: "toolbar_____",
                title: bookmark.title,
                url: bookmark.url
              }).then((newBrowserBookmark) => {
                console.log("Added to browser:", newBrowserBookmark.title);
                bookmarksCollectionRef.doc(firestoreId).update({ browserBookmarkId: newBrowserBookmark.id });
              }).catch(error => console.error("Error creating browser bookmark:", error));
            } else {
              if (!browserBookmarkId) {
                bookmarksCollectionRef.doc(firestoreId).update({ browserBookmarkId: existingBookmarks[0].id });
              }
            }
          }).catch(error => console.error("Error searching browser bookmarks:", error));
        }
      } else if (change.type === "modified") {
        if (bookmark.isDeleted && browserBookmarkId) {
          browser.bookmarks.remove(browserBookmarkId)
            .then(() => console.log("Removed from browser (soft-deleted in Firestore):", bookmark.title))
            .catch(error => console.error("Error removing browser bookmark on soft delete:", error));
        } else if (!bookmark.isDeleted && browserBookmarkId) {
          browser.bookmarks.update(browserBookmarkId, {
            title: bookmark.title,
            url: bookmark.url
          }).then(() => console.log("Updated in browser:", bookmark.title))
            .catch(error => console.error("Error updating browser bookmark:", error));
        }
      } else if (change.type === "removed") {
        if (browserBookmarkId) {
          browser.bookmarks.remove(browserBookmarkId)
            .then(() => console.log("Removed from browser (permanently deleted from Firestore):", bookmark.title))
            .catch(error => console.error("Error removing browser bookmark on hard delete:", error));
        }
      }
    });
  });
  console.log("Firestore listener attached for user:", uid);
}

// ---- Listen for browser bookmark events, sync back to Firestore ----
browser.bookmarks.onCreated.addListener((id, bookmark) => {
  if (auth.currentUser) {
    db.collection(`users/${auth.currentUser.uid}/bookmarks`)
      .where("url", "==", bookmark.url).get().then(snapshot => {
        if (snapshot.empty) {
          const newBookmarkRef = db.collection(`users/${auth.currentUser.uid}/bookmarks`).doc();
          newBookmarkRef.set({
            title: bookmark.title,
            url: bookmark.url,
            browserBookmarkId: bookmark.id,
            addDate: firebase.firestore.FieldValue.serverTimestamp(),
            isFavorite: false,
            isReadingList: false,
            isDeleted: false,
            deletedTimestamp: null,
            source: 'browser_extension'
          }).then(() => console.log("Pushed new browser bookmark to Firestore:", bookmark.title))
            .catch(error => console.error("Error pushing browser bookmark to Firestore:", error));
        } else {
          snapshot.docs[0].ref.update({ browserBookmarkId: bookmark.id }, { merge: true });
        }
      })
      .catch(error => console.error("Error checking existing bookmark in Firestore:", error));
  }
});

browser.bookmarks.onRemoved.addListener((id, removeInfo) => {
  if (auth.currentUser) {
    db.collection(`users/${auth.currentUser.uid}/bookmarks`)
      .where("browserBookmarkId", "==", id)
      .get()
      .then(snapshot => {
        if (!snapshot.empty) {
          snapshot.docs[0].ref.update({
            isDeleted: true,
            deletedTimestamp: firebase.firestore.FieldValue.serverTimestamp()
          });
          console.log("Soft-deleted bookmark in Firestore from browser removal:", id);
        }
      })
      .catch(error => console.error("Error handling browser bookmark removal:", error));
  }
});

browser.bookmarks.onChanged.addListener((id, changeInfo) => {
  if (auth.currentUser) {
    db.collection(`users/${auth.currentUser.uid}/bookmarks`)
      .where("browserBookmarkId", "==", id)
      .get()
      .then(snapshot => {
        if (!snapshot.empty) {
          const updateData = {};
          if (changeInfo.title !== undefined) updateData.title = changeInfo.title;
          if (changeInfo.url !== undefined) updateData.url = changeInfo.url;
          if (Object.keys(updateData).length > 0) {
            snapshot.docs[0].ref.update(updateData);
            console.log("Updated bookmark in Firestore from browser change:", id);
          }
        }
      })
      .catch(error => console.error("Error handling browser bookmark change:", error));
  }
});

// ---- Firebase Auth state changes ----
auth.onAuthStateChanged(user => {
  if (user) {
    console.log("Auth state changed: User is signed in.", user.uid);
    setupFirestoreListener(user.uid);
    browser.storage.local.set({ currentUser: { uid: user.uid, email: user.email } });
  } else {
    console.log("Auth state changed: User is signed out.");
    browser.storage.local.remove("currentUser");
    if (firestoreListenerUnsubscribe) {
      firestoreListenerUnsubscribe();
      firestoreListenerUnsubscribe = null;
    }
  }
});

// ---- Message listener for popup.js communication ----
browser.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === "signInWithGoogle") {
    signInWithGoogle()
      .then(user => sendResponse({ success: true, user: user }))
      .catch(error => sendResponse({ success: false, message: error.message }));
    return true;
  } else if (request.action === "signOut") {
    signOutUser()
      .then(() => sendResponse({ success: true }))
      .catch(error => sendResponse({ success: false, message: error.message }));
    return true;
  } else if (request.action === "saveCurrentPage") {
    browser.tabs.query({ active: true, currentWindow: true })
      .then(tabs => {
        const currentTab = tabs[0];
        if (currentTab && currentTab.url) {
          const bookmarkData = {
            title: currentTab.title || currentTab.url,
            url: currentTab.url,
          };
          return saveBookmarkToFirestore(bookmarkData);
        } else {
          throw new Error("No active tab found.");
        }
      })
      .then(savedBookmark => sendResponse({ success: true, bookmark: savedBookmark }))
      .catch(error => sendResponse({ success: false, message: error.message }));
    return true;
  } else if (request.action === "getCurrentUser") {
    const user = auth.currentUser;
    if (user) {
      sendResponse({ user: { uid: user.uid, email: user.email } });
    } else {
      sendResponse({ user: null });
    }
    return true;
  } else if (request.action === "getLockerBookmarks") {
    const user = auth.currentUser;
    if (!user) {
      sendResponse({ success: false, message: "Not signed in." });
      return true;
    }
    db.collection(`users/${user.uid}/bookmarks`)
      .where("isDeleted", "==", false)
      .orderBy("addDate", "desc")
      .limit(10)
      .get()
      .then(snapshot => {
        const bookmarks = snapshot.docs.map(doc => doc.data());
        sendResponse({ success: true, bookmarks: bookmarks });
      })
      .catch(error => sendResponse({ success: false, message: error.message }));
    return true; // Added 'return true;' to ensure async response is handled
  } else if (request.action === "deleteLockerBookmark") {
      deleteBookmarkFromFirestore(request.bookmarkId)
          .then(() => sendResponse({ success: true }))
          .catch(error => sendResponse({ success: false, message: error.message }));
      return true; // Added 'return true;' to ensure async response is handled
  }
});
