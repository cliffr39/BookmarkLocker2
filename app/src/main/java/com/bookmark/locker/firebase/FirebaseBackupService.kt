package com.bookmark.locker.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBackupService @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId: String? get() = FirebaseAuth.getInstance().currentUser?.uid

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
}

