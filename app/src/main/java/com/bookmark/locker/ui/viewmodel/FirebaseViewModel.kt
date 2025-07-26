package com.bookmark.locker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.repository.BookmarkRepository
import com.bookmark.locker.firebase.FirebaseAuthService
import com.bookmark.locker.firebase.FirebaseBackupService
import com.bookmark.locker.firebase.SyncResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val backupService: FirebaseBackupService,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _conflictState = MutableStateFlow<ConflictState?>(null)
    val conflictState: StateFlow<ConflictState?> = _conflictState.asStateFlow()

    private var backgroundSyncJob: Job? = null
    private var realTimeSyncJob: Job? = null

    init {
        viewModelScope.launch {
            authService.getCurrentUserFlow().collect { user ->
                _authState.value = if (user != null) {
                    AuthState.SignedIn(user)
                } else {
                    AuthState.SignedOut
                }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.signInAnonymously()
            _authState.value = if (result.isSuccess) {
                AuthState.SignedIn(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Anonymous sign-in failed")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.signInWithEmailAndPassword(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.SignedIn(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Sign-in failed")
            }
        }
    }

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authService.createUserWithEmailAndPassword(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.SignedIn(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Account creation failed")
            }
        }
    }

    fun signOut() {
        authService.signOut()
    }
    
    // Google Sign-In methods
    fun getGoogleSignInClient(): GoogleSignInClient {
        return authService.getGoogleSignInClient()
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            android.util.Log.d("FirebaseViewModel", "Starting Google Sign-In with account: ${account.email}")
            _authState.value = AuthState.Loading
            val result = authService.signInWithGoogle(account)
            _authState.value = if (result.isSuccess) {
                android.util.Log.d("FirebaseViewModel", "Google Sign-In successful")
                AuthState.SignedIn(result.getOrNull()!!)
            } else {
                android.util.Log.e("FirebaseViewModel", "Google Sign-In failed: ${result.exceptionOrNull()?.message}")
                AuthState.Error(result.exceptionOrNull()?.message ?: "Google sign-in failed")
            }
        }
    }

    fun backupBookmarks() {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                // Backup folders first
                val folders = bookmarkRepository.getAllFolders().first()
                val folderMaps = folders.map { folder ->
                    mapOf<String, Any>(
                        "id" to folder.id.toString(),
                        "name" to folder.name,
                        "parentId" to (folder.parentId?.toString() ?: ""),
                        "dateCreated" to folder.dateCreated,
                        "dateModified" to folder.dateModified,
                        "color" to (folder.color ?: ""),
                        "icon" to (folder.icon ?: "")
                    )
                }
                
                val folderResult = backupService.backupFolders(folderMaps)
                if (folderResult.isFailure) {
                    _backupState.value = BackupState.Error(folderResult.exceptionOrNull()?.message ?: "Folder backup failed")
                    return@launch
                }
                
                // Backup ALL bookmarks (including deleted ones)
                val activeBookmarks = bookmarkRepository.getAllBookmarksWithTags().first()
                val deletedBookmarks = bookmarkRepository.getDeletedBookmarks().first()
                
                val allBookmarkMaps = mutableListOf<Map<String, Any>>()
                
                // Add active bookmarks
                activeBookmarks.forEach { bookmarkWithTags ->
                    allBookmarkMaps.add(
                        mapOf<String, Any>(
                            "id" to bookmarkWithTags.bookmark.id.toString(),
                            "title" to bookmarkWithTags.bookmark.title,
                            "url" to bookmarkWithTags.bookmark.url,
                            "description" to (bookmarkWithTags.bookmark.description ?: ""),
                            "folderId" to (bookmarkWithTags.bookmark.folderId?.toString() ?: ""),
                            "isFavorite" to bookmarkWithTags.bookmark.isFavorite,
                            "isInReadingList" to bookmarkWithTags.bookmark.isInReadingList,
                            "createdAt" to bookmarkWithTags.bookmark.dateAdded,
                            "dateModified" to bookmarkWithTags.bookmark.dateModified,
                            "isDeleted" to bookmarkWithTags.bookmark.isDeleted,
                            "deletedAt" to (bookmarkWithTags.bookmark.deletedAt ?: 0L),
                            "tags" to bookmarkWithTags.tags.map { tag -> tag.name }
                        )
                    )
                }
                
                // Add deleted bookmarks
                deletedBookmarks.forEach { bookmark ->
                    allBookmarkMaps.add(
                        mapOf<String, Any>(
                            "id" to bookmark.id.toString(),
                            "title" to bookmark.title,
                            "url" to bookmark.url,
                            "description" to (bookmark.description ?: ""),
                            "folderId" to (bookmark.folderId?.toString() ?: ""),
                            "isFavorite" to bookmark.isFavorite,
                            "isInReadingList" to bookmark.isInReadingList,
                            "createdAt" to bookmark.dateAdded,
                            "dateModified" to bookmark.dateModified,
                            "isDeleted" to bookmark.isDeleted,
                            "deletedAt" to (bookmark.deletedAt ?: 0L),
                            "tags" to emptyList<String>() // Deleted bookmarks don't have tags in the current query
                        )
                    )
                }
                
                val bookmarkResult = backupService.backupBookmarks(allBookmarkMaps)
                _backupState.value = if (bookmarkResult.isSuccess) {
                    BackupState.Success("Bookmarks and folders backed up successfully")
                } else {
                    BackupState.Error(bookmarkResult.exceptionOrNull()?.message ?: "Bookmark backup failed")
                }
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Backup failed")
            }
        }
    }

    fun restoreBookmarks() {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                // Restore folders first
                val folderResult = backupService.restoreFolders()
                if (folderResult.isSuccess) {
                    val folderMaps = folderResult.getOrNull() ?: emptyList()
                    folderMaps.forEach { folderMap ->
                        try {
                            val folder = Folder(
                                id = (folderMap["id"] as? String)?.toLongOrNull() ?: 0L,
                                name = folderMap["name"] as? String ?: "",
                                parentId = (folderMap["parentId"] as? String)?.toLongOrNull(),
                                dateCreated = folderMap["dateCreated"] as? Long ?: System.currentTimeMillis(),
                                dateModified = folderMap["dateModified"] as? Long ?: System.currentTimeMillis(),
                                color = folderMap["color"] as? String,
                                icon = folderMap["icon"] as? String
                            )
                            bookmarkRepository.insertFolder(folder)
                        } catch (e: Exception) {
                            // Skip invalid folders
                        }
                    }
                }
                
                // Restore bookmarks
                val bookmarkResult = backupService.restoreBookmarks()
                if (bookmarkResult.isSuccess) {
                    val bookmarkMaps = bookmarkResult.getOrNull() ?: emptyList()
                    // Convert maps back to bookmarks and save to local database
                    bookmarkMaps.forEach { bookmarkMap ->
                        try {
                            val bookmark = Bookmark(
                                id = (bookmarkMap["id"] as? String)?.toLongOrNull() ?: 0L,
                                title = bookmarkMap["title"] as? String ?: "",
                                url = bookmarkMap["url"] as? String ?: "",
                                description = bookmarkMap["description"] as? String,
                                folderId = (bookmarkMap["folderId"] as? String)?.toLongOrNull(),
                                isInReadingList = bookmarkMap["isInReadingList"] as? Boolean ?: false,
                                isFavorite = bookmarkMap["isFavorite"] as? Boolean ?: false,
                                dateAdded = bookmarkMap["createdAt"] as? Long ?: System.currentTimeMillis(),
                                dateModified = bookmarkMap["dateModified"] as? Long ?: System.currentTimeMillis(),
                                isDeleted = bookmarkMap["isDeleted"] as? Boolean ?: false,
                                deletedAt = (bookmarkMap["deletedAt"] as? Long)?.takeIf { it > 0 }
                            )
                            // Insert bookmark (this will handle duplicates based on your repository logic)
                            bookmarkRepository.insertBookmark(bookmark)
                        } catch (e: Exception) {
                            // Skip invalid bookmarks
                        }
                    }
                    _backupState.value = BackupState.Success("Bookmarks and folders restored successfully")
                } else {
                    _backupState.value = BackupState.Error(bookmarkResult.exceptionOrNull()?.message ?: "Restore failed")
                }
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Restore failed")
            }
        }
    }

    fun clearBackupState() {
        _backupState.value = BackupState.Idle
    }

    fun clearAuthError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.SignedOut
        }
    }

    // Background sync methods
    fun startBackgroundSync() {
        if (_authState.value !is AuthState.SignedIn) return
        
        backgroundSyncJob?.cancel()
        backgroundSyncJob = viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            try {
                // Sync all bookmarks individually
                val activeBookmarks = bookmarkRepository.getAllBookmarksWithTags().first()
                val deletedBookmarks = bookmarkRepository.getDeletedBookmarks().first()
                
                val allBookmarks = activeBookmarks.map { it.bookmark } + deletedBookmarks
                
                allBookmarks.forEach { bookmark ->
                    val bookmarkMap = mapOf<String, Any>(
                        "id" to bookmark.id.toString(),
                        "title" to bookmark.title,
                        "url" to bookmark.url,
                        "description" to (bookmark.description ?: ""),
                        "folderId" to (bookmark.folderId?.toString() ?: ""),
                        "isFavorite" to bookmark.isFavorite,
                        "isInReadingList" to bookmark.isInReadingList,
                        "createdAt" to bookmark.dateAdded,
                        "dateModified" to bookmark.dateModified,
                        "isDeleted" to bookmark.isDeleted,
                        "deletedAt" to (bookmark.deletedAt ?: 0L)
                    )
                    
                    val result = backupService.syncBookmark(bookmarkMap)
                    if (result.isSuccess) {
                        when (val syncResult = result.getOrNull()!!) {
                            is SyncResult.RemoteNewer -> {
                                // Update local with remote data
                                updateLocalBookmarkFromRemote(syncResult.remoteData)
                            }
                            is SyncResult.Conflict -> {
                                // Show conflict dialog
                                _conflictState.value = ConflictState.BookmarkConflict(
                                    bookmarkId = bookmark.id,
                                    local = syncResult.localData,
                                    remote = syncResult.remoteData
                                )
                                return@forEach // Stop syncing until conflict is resolved
                            }
                            else -> { /* Created or Updated - no action needed */ }
                        }
                    }
                }
                
                // Sync folders
                val folders = bookmarkRepository.getAllFolders().first()
                folders.forEach { folder ->
                    val folderMap = mapOf<String, Any>(
                        "id" to folder.id.toString(),
                        "name" to folder.name,
                        "parentId" to (folder.parentId?.toString() ?: ""),
                        "dateCreated" to folder.dateCreated,
                        "dateModified" to folder.dateModified,
                        "color" to (folder.color ?: ""),
                        "icon" to (folder.icon ?: "")
                    )
                    
                    val result = backupService.syncFolder(folderMap)
                    if (result.isSuccess) {
                        when (val syncResult = result.getOrNull()!!) {
                            is SyncResult.RemoteNewer -> {
                                updateLocalFolderFromRemote(syncResult.remoteData)
                            }
                            is SyncResult.Conflict -> {
                                _conflictState.value = ConflictState.FolderConflict(
                                    folderId = folder.id,
                                    local = syncResult.localData,
                                    remote = syncResult.remoteData
                                )
                                return@forEach
                            }
                            else -> { /* Created or Updated - no action needed */ }
                        }
                    }
                }
                
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }
    
    fun startRealtimeSync() {
        if (_authState.value !is AuthState.SignedIn) return
        
        realTimeSyncJob?.cancel()
        realTimeSyncJob = viewModelScope.launch {
            try {
                backupService.startBookmarkSync().collect { remoteBookmarks ->
                    remoteBookmarks.forEach { remoteBookmarkMap ->
                        updateLocalBookmarkFromRemote(remoteBookmarkMap)
                    }
                }
            } catch (e: Exception) {
                _syncState.value = SyncState.Error("Real-time sync failed: ${e.message}")
            }
        }
        
        viewModelScope.launch {
            try {
                backupService.startFolderSync().collect { remoteFolders ->
                    remoteFolders.forEach { remoteFolderMap ->
                        updateLocalFolderFromRemote(remoteFolderMap)
                    }
                }
            } catch (e: Exception) {
                _syncState.value = SyncState.Error("Real-time folder sync failed: ${e.message}")
            }
        }
    }
    
    fun stopSync() {
        backgroundSyncJob?.cancel()
        realTimeSyncJob?.cancel()
        backupService.stopSync()
        _syncState.value = SyncState.Idle
    }
    
    // Conflict resolution methods
    fun resolveBookmarkConflict(useLocal: Boolean) {
        val conflict = _conflictState.value as? ConflictState.BookmarkConflict ?: return
        
        viewModelScope.launch {
            if (useLocal) {
                // Force upload local version
                val result = backupService.syncBookmark(conflict.local)
                // Handle result if needed
            } else {
                // Use remote version
                updateLocalBookmarkFromRemote(conflict.remote)
            }
            _conflictState.value = null
            // Continue background sync if it was running
            if (backgroundSyncJob?.isActive == true) {
                startBackgroundSync()
            }
        }
    }
    
    fun resolveFolderConflict(useLocal: Boolean) {
        val conflict = _conflictState.value as? ConflictState.FolderConflict ?: return
        
        viewModelScope.launch {
            if (useLocal) {
                val result = backupService.syncFolder(conflict.local)
                // Handle result if needed
            } else {
                updateLocalFolderFromRemote(conflict.remote)
            }
            _conflictState.value = null
            if (backgroundSyncJob?.isActive == true) {
                startBackgroundSync()
            }
        }
    }
    
    private suspend fun updateLocalBookmarkFromRemote(remoteData: Map<String, Any>?) {
        remoteData?.let { data ->
            try {
                val bookmark = Bookmark(
                    id = (data["id"] as? String)?.toLongOrNull() ?: 0L,
                    title = data["title"] as? String ?: "",
                    url = data["url"] as? String ?: "",
                    description = data["description"] as? String,
                    folderId = (data["folderId"] as? String)?.toLongOrNull(),
                    isInReadingList = data["isInReadingList"] as? Boolean ?: false,
                    isFavorite = data["isFavorite"] as? Boolean ?: false,
                    dateAdded = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                    dateModified = data["dateModified"] as? Long ?: System.currentTimeMillis(),
                    isDeleted = data["isDeleted"] as? Boolean ?: false,
                    deletedAt = (data["deletedAt"] as? Long)?.takeIf { it > 0 }
                )
                bookmarkRepository.insertBookmark(bookmark)
            } catch (e: Exception) {
                // Log error but continue
                android.util.Log.e("FirebaseViewModel", "Failed to update local bookmark from remote", e)
            }
        }
    }
    
    private suspend fun updateLocalFolderFromRemote(remoteData: Map<String, Any>?) {
        remoteData?.let { data ->
            try {
                val folder = Folder(
                    id = (data["id"] as? String)?.toLongOrNull() ?: 0L,
                    name = data["name"] as? String ?: "",
                    parentId = (data["parentId"] as? String)?.toLongOrNull(),
                    dateCreated = data["dateCreated"] as? Long ?: System.currentTimeMillis(),
                    dateModified = data["dateModified"] as? Long ?: System.currentTimeMillis(),
                    color = data["color"] as? String,
                    icon = data["icon"] as? String
                )
                bookmarkRepository.insertFolder(folder)
            } catch (e: Exception) {
                android.util.Log.e("FirebaseViewModel", "Failed to update local folder from remote", e)
            }
        }
    }
    
    fun clearSyncState() {
        _syncState.value = SyncState.Idle
    }
    
    fun dismissConflict() {
        _conflictState.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopSync()
    }
}

sealed class AuthState {
    object SignedOut : AuthState()
    object Loading : AuthState()
    data class SignedIn(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class BackupState {
    object Idle : BackupState()
    object Loading : BackupState()
    data class Success(val message: String) : BackupState()
    data class Error(val message: String) : BackupState()
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}

sealed class ConflictState {
    data class BookmarkConflict(
        val bookmarkId: Long,
        val local: Map<String, Any>,
        val remote: Map<String, Any>?
    ) : ConflictState()
    
    data class FolderConflict(
        val folderId: Long,
        val local: Map<String, Any>,
        val remote: Map<String, Any>?
    ) : ConflictState()
}
