package com.bookmark.locker.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBackupService @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId: String? get() = FirebaseAuth.getInstance().currentUser?.uid
    private var bookmarksListener: ListenerRegistration? = null
    private var foldersListener: ListenerRegistration? = null

    suspend fun backupBookmarks(bookmarks: List<Map<String, Any>>): Result<Unit> {
        return try {
            currentUserId?.let { userId ->
                val bookmarksRef = firestore.collection("users").document(userId).collection("bookmarks")
                firestore.runBatch { batch ->
                    bookmarks.forEach { bookmark ->
                        val docId = bookmark["id"] as String
                        batch.set(bookmarksRef.document(docId), bookmark, SetOptions.merge())
                    }
                }.await()
                Result.success(Unit)
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun backupFolders(folders: List<Map<String, Any>>): Result<Unit> {
        return try {
            currentUserId?.let { userId ->
                val foldersRef = firestore.collection("users").document(userId).collection("folders")
                firestore.runBatch { batch ->
                    folders.forEach { folder ->
                        val docId = folder["id"] as String
                        batch.set(foldersRef.document(docId), folder, SetOptions.merge())
                    }
                }.await()
                Result.success(Unit)
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreBookmarks(): Result<List<Map<String, Any>>> {
        return try {
            currentUserId?.let { userId ->
                val bookmarksRef = firestore.collection("users").document(userId).collection("bookmarks")
                val snapshot = bookmarksRef.get().await()
                val bookmarks = snapshot.documents.map { it.data?.toMap() ?: emptyMap() }
                Result.success(bookmarks)
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFolders(): Result<List<Map<String, Any>>> {
        return try {
            currentUserId?.let { userId ->
                val foldersRef = firestore.collection("users").document(userId).collection("folders")
                val snapshot = foldersRef.get().await()
                val folders = snapshot.documents.map { it.data?.toMap() ?: emptyMap() }
                Result.success(folders)
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Real-time sync methods
    fun startBookmarkSync(): Flow<List<Map<String, Any>>> = callbackFlow {
        currentUserId?.let { userId ->
            val bookmarksRef = firestore.collection("users").document(userId).collection("bookmarks")
            bookmarksListener = bookmarksRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val bookmarks = snapshot.documents.mapNotNull { doc ->
                        doc.data?.toMutableMap()?.apply {
                            this["id"] = doc.id
                        }
                    }
                    trySend(bookmarks)
                }
            }
        } ?: close(IllegalStateException("User not authenticated"))
        
        awaitClose {
            bookmarksListener?.remove()
            bookmarksListener = null
        }
    }

    fun startFolderSync(): Flow<List<Map<String, Any>>> = callbackFlow {
        currentUserId?.let { userId ->
            val foldersRef = firestore.collection("users").document(userId).collection("folders")
            foldersListener = foldersRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val folders = snapshot.documents.mapNotNull { doc ->
                        doc.data?.toMutableMap()?.apply {
                            this["id"] = doc.id
                        }
                    }
                    trySend(folders)
                }
            }
        } ?: close(IllegalStateException("User not authenticated"))
        
        awaitClose {
            foldersListener?.remove()
            foldersListener = null
        }
    }

    fun stopSync() {
        bookmarksListener?.remove()
        foldersListener?.remove()
        bookmarksListener = null
        foldersListener = null
    }

    // Background sync - upload single bookmark/folder with conflict detection
    suspend fun syncBookmark(bookmark: Map<String, Any>): Result<SyncResult> {
        return try {
            currentUserId?.let { userId ->
                val bookmarkId = bookmark["id"] as String
                val bookmarkRef = firestore.collection("users").document(userId)
                    .collection("bookmarks").document(bookmarkId)
                
                val existingDoc = bookmarkRef.get().await()
                
                if (existingDoc.exists()) {
                    val existingData = existingDoc.data
                    val existingModified = existingData?.get("dateModified") as? Long ?: 0L
                    val newModified = bookmark["dateModified"] as? Long ?: 0L
                    
                    when {
                        newModified > existingModified -> {
                            // Local is newer, update remote
                            bookmarkRef.set(bookmark, SetOptions.merge()).await()
                            Result.success(SyncResult.Updated)
                        }
                        newModified < existingModified -> {
                            // Remote is newer, return remote data
                            Result.success(SyncResult.RemoteNewer(existingData))
                        }
                        else -> {
                            // Same timestamp, need user resolution
                            Result.success(SyncResult.Conflict(existingData, bookmark))
                        }
                    }
                } else {
                    // New bookmark, just add it
                    bookmarkRef.set(bookmark, SetOptions.merge()).await()
                    Result.success(SyncResult.Created)
                }
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncFolder(folder: Map<String, Any>): Result<SyncResult> {
        return try {
            currentUserId?.let { userId ->
                val folderId = folder["id"] as String
                val folderRef = firestore.collection("users").document(userId)
                    .collection("folders").document(folderId)
                
                val existingDoc = folderRef.get().await()
                
                if (existingDoc.exists()) {
                    val existingData = existingDoc.data
                    val existingModified = existingData?.get("dateModified") as? Long ?: 0L
                    val newModified = folder["dateModified"] as? Long ?: 0L
                    
                    when {
                        newModified > existingModified -> {
                            // Local is newer, update remote
                            folderRef.set(folder, SetOptions.merge()).await()
                            Result.success(SyncResult.Updated)
                        }
                        newModified < existingModified -> {
                            // Remote is newer, return remote data
                            Result.success(SyncResult.RemoteNewer(existingData))
                        }
                        else -> {
                            // Same timestamp, need user resolution
                            Result.success(SyncResult.Conflict(existingData, folder))
                        }
                    }
                } else {
                    // New folder, just add it
                    folderRef.set(folder, SetOptions.merge()).await()
                    Result.success(SyncResult.Created)
                }
            } ?: Result.failure(Exception("User not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

sealed class SyncResult {
    object Created : SyncResult()
    object Updated : SyncResult()
    data class RemoteNewer(val remoteData: Map<String, Any>?) : SyncResult()
    data class Conflict(val remoteData: Map<String, Any>?, val localData: Map<String, Any>) : SyncResult()
}

