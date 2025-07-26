package com.bookmark.locker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.repository.BookmarkRepository
import com.bookmark.locker.firebase.FirebaseAuthService
import com.bookmark.locker.firebase.FirebaseBackupService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
                
                // Backup bookmarks
                val bookmarks = bookmarkRepository.getAllBookmarksWithTags().first()
                val bookmarkMaps = bookmarks.map { bookmarkWithTags: BookmarkWithTags ->
                    mapOf<String, Any>(
                        "id" to bookmarkWithTags.bookmark.id.toString(),
                        "title" to bookmarkWithTags.bookmark.title,
                        "url" to bookmarkWithTags.bookmark.url,
                        "description" to (bookmarkWithTags.bookmark.description ?: ""),
                        "folderId" to (bookmarkWithTags.bookmark.folderId?.toString() ?: ""),
                        "isFavorite" to bookmarkWithTags.bookmark.isFavorite,
                        "isInReadingList" to bookmarkWithTags.bookmark.isInReadingList,
                        "createdAt" to bookmarkWithTags.bookmark.dateAdded,
                        "updatedAt" to bookmarkWithTags.bookmark.dateModified,
                        "tags" to bookmarkWithTags.tags.map { tag -> tag.name }
                    )
                }
                
                val bookmarkResult = backupService.backupBookmarks(bookmarkMaps)
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
                                dateModified = bookmarkMap["updatedAt"] as? Long ?: System.currentTimeMillis()
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
