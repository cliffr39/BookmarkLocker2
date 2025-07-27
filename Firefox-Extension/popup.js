// bookmark-locker-extension/popup.js
document.addEventListener('DOMContentLoaded', () => {
  const authStatusDiv = document.getElementById('auth-status');
  const googleSignInButton = document.getElementById('google-signin-button');
  const signoutButton = document.getElementById('signout-button');
  const saveBookmarkButton = document.getElementById('save-bookmark-button');
  const bookmarksList = document.getElementById('bookmarks-list');
  const mainActionsDiv = document.getElementById('main-actions');
  const authSectionDiv = document.getElementById('auth-section');

  // Function to update UI based on auth state
  function updateUI(user) {
    if (user) {
      authStatusDiv.textContent = `Signed in as: ${user.email || 'Anonymous'}`;
      authSectionDiv.style.display = 'none'; // Hide sign-in button
      mainActionsDiv.style.display = 'block'; // Show main actions
      signoutButton.style.display = 'block'; // Ensure sign out button is visible

      // Fetch and display bookmarks for the signed-in user
      fetchLockerBookmarks();

    } else {
      authStatusDiv.textContent = 'Not signed in.';
      authSectionDiv.style.display = 'block'; // Show sign-in button
      mainActionsDiv.style.display = 'none'; // Hide main actions
      signoutButton.style.display = 'none'; // Hide sign out button
      bookmarksList.innerHTML = '<p class="text-gray-400 text-sm text-center">Sign in to see your bookmarks.</p>';
    }
  }

  // Function to fetch and display bookmarks from Firestore
  function fetchLockerBookmarks() {
    bookmarksList.innerHTML = '<p class="text-gray-400 text-sm text-center">Loading bookmarks...</p>';
    browser.runtime.sendMessage({ action: "getLockerBookmarks" })
      .then(response => {
        if (response && response.success) {
          bookmarksList.innerHTML = ''; // Clear loading message
          if (response.bookmarks.length === 0) {
            bookmarksList.innerHTML = '<p class="text-gray-400 text-sm text-center">No bookmarks found in your Locker.</p>';
          } else {
            response.bookmarks.forEach(bookmark => {
              const bookmarkItem = document.createElement('div');
              bookmarkItem.className = 'bookmark-item';
              bookmarkItem.innerHTML = `
                <img src="${getFaviconUrl(bookmark.url)}" alt="Favicon" onerror="this.onerror=null;this.src='icons/default_favicon.png';">
                <div class="bookmark-info">
                  <div class="bookmark-title">${bookmark.title}</div>
                  <div class="bookmark-url">${bookmark.url}</div>
                </div>
                <div class="bookmark-actions">
                  <button data-id="${bookmark.id}" class="delete-button">Delete</button>
                </div>
              `;
              bookmarkItem.addEventListener('click', () => {
                browser.tabs.create({ url: bookmark.url }); // Open bookmark in new tab
              });
              bookmarksList.appendChild(bookmarkItem);
            });

            // Add event listeners for delete buttons
            document.querySelectorAll('.delete-button').forEach(button => {
                button.addEventListener('click', (event) => {
                    event.stopPropagation(); // Prevent opening the link when delete button is clicked
                    const bookmarkId = button.dataset.id;
                    if (confirm('Are you sure you want to delete this bookmark?')) {
                        browser.runtime.sendMessage({ action: "deleteLockerBookmark", bookmarkId: bookmarkId })
                            .then(response => {
                                if (response && response.success) {
                                    alert('Bookmark deleted!');
                                    fetchLockerBookmarks(); // Refresh list
                                } else {
                                    alert('Failed to delete bookmark: ' + (response ? response.message : 'Unknown error'));
                                }
                            })
                            .catch(error => console.error('Error sending delete message:', error));
                    }
                });
            });

          }
        } else {
          bookmarksList.innerHTML = `<p class="text-red-400 text-sm text-center">Error loading bookmarks: ${response ? response.message : 'Unknown error'}</p>`;
        }
      })
      .catch(error => {
        console.error("Error fetching bookmarks:", error);
        bookmarksList.innerHTML = `<p class="text-red-400 text-sm text-center">Network error: ${error.message}</p>`;
      });
  }

  // Helper to get favicon URL
  function getFaviconUrl(url) {
    try {
      const parsedUrl = new URL(url);
      return `https://www.google.com/s2/favicons?domain=${parsedUrl.hostname}&sz=32`;
    } catch (e) {
      return 'icons/default_favicon.png'; // Fallback
    }
  }

  // Initial UI update based on current auth state from background script
  browser.runtime.sendMessage({ action: "getCurrentUser" })
    .then(response => {
      updateUI(response.user);
    })
    .catch(error => {
      console.error("Error getting current user:", error);
      updateUI(null);
    });

  googleSignInButton.addEventListener('click', () => {
    browser.runtime.sendMessage({ action: "signInWithGoogle" })
      .then(response => {
        if (response && response.success) {
          updateUI(response.user);
        } else {
          alert("Google Sign-In failed or cancelled: " + (response ? response.message : "Unknown error"));
        }
      })
      .catch(error => console.error("Error sending sign-in message:", error));
  });

  signoutButton.addEventListener('click', () => {
    browser.runtime.sendMessage({ action: "signOut" })
      .then(response => {
        if (response && response.success) {
          updateUI(null);
        } else {
          alert("Sign Out failed: " + (response ? response.message : "Unknown error"));
        }
      })
      .catch(error => console.error("Error sending sign-out message:", error));
  });

  saveBookmarkButton.addEventListener('click', () => {
    browser.runtime.sendMessage({ action: "saveCurrentPage" })
      .then(response => {
        if (response && response.success) {
          alert("Current page saved to Bookmark Locker!");
          fetchLockerBookmarks(); // Refresh the list after saving
        } else {
          alert("Failed to save bookmark: " + (response ? response.message : "Unknown error"));
        }
      })
      .catch(error => console.error("Error sending save bookmark message:", error));
  });
});
