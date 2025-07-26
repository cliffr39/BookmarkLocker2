package com.bookmark.locker.ui.screen

import android.content.Intent
import android.net.Uri
import com.bookmark.locker.ui.WebViewActivity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bookmark.locker.R
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.permission.PermissionManager
import com.bookmark.locker.ui.components.*
import com.bookmark.locker.ui.viewmodel.BookmarkTab
import com.bookmark.locker.ui.viewmodel.BookmarkViewModel
import com.bookmark.locker.ui.viewmodel.UpdateCheckerViewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    updateCheckerViewModel: UpdateCheckerViewModel = hiltViewModel(),
    sharedTitle: String? = null,
    sharedUrl: String? = null,
    permissionManager: PermissionManager
) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentBookmarks by viewModel.currentBookmarks.collectAsState()
    val bookmarksWithTags by viewModel.filteredBookmarksWithTags.collectAsState()
    val allTags by viewModel.allTags.collectAsState()
    val selectedTagFilters by viewModel.selectedTagFilters.collectAsState()
    val isTagFilterActive by viewModel.isTagFilterActive.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedBookmarks by viewModel.selectedBookmarks.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val pendingUndoAction by viewModel.pendingUndoAction.collectAsState()
    
    // Update checker state
    val updateState by updateCheckerViewModel.updateState.collectAsState()
    
    var showAddBookmarkDialog by remember { mutableStateOf(false) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var editingBookmark by remember { mutableStateOf<Bookmark?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showTagManagement by remember { mutableStateOf(false) }
    var showRecentlyDeleted by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showBackupRestore by remember { mutableStateOf(false) }
    var showReminders by remember { mutableStateOf(false) }
    var cameFromSettings by remember { mutableStateOf(false) }
    var showFolderSelectionDialog by remember { mutableStateOf(false) }
    var showBookmarkSelectionDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showFirebaseAuth by remember { mutableStateOf(false) }
    var currentFolder by remember { mutableStateOf<com.bookmark.locker.data.model.Folder?>(null) }
    
    // Get permission states
    val hasNotificationPermission by permissionManager.hasNotificationPermission.collectAsState()
    val hasAlarmPermission by permissionManager.hasExactAlarmPermission.collectAsState()
    
    // Check permissions on startup
    LaunchedEffect(Unit) {
        android.util.Log.d("BookmarkScreen", "Checking permissions...")
        android.util.Log.d("BookmarkScreen", "Has notification: $hasNotificationPermission")
        android.util.Log.d("BookmarkScreen", "Has alarm: $hasAlarmPermission")
        android.util.Log.d("BookmarkScreen", "Android API: ${android.os.Build.VERSION.SDK_INT}")
        
        // Show permission dialog if we don't have all required permissions
        if (!permissionManager.hasAllPermissions()) {
            android.util.Log.d("BookmarkScreen", "Not all permissions granted, showing dialog")
            showPermissionDialog = true
        }
    }
    
    // File export/import launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            viewModel.exportBookmarks(
                onSuccess = { csvContent ->
                    try {
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            OutputStreamWriter(outputStream).use { writer ->
                                writer.write(csvContent)
                                writer.flush()
                            }
                        }
                        Toast.makeText(context, "Bookmarks exported successfully!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val content = reader.readText()
                        viewModel.importBookmarks(
                            csvContent = content,
                            onSuccess = { count ->
                                Toast.makeText(context, "Imported $count bookmarks", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Handle shared intents - show dialog instead of directly saving
    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrEmpty()) {
            showAddBookmarkDialog = true
        }
    }
    
    // Function to open URL in browser
    fun openBookmarkInBrowser(url: String) {
        try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error - could show a toast
        }
    }
    
    // Function to open reading list items in WebView
    fun openInWebView(url: String) {
        try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra("url", formattedUrl)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening page: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Show Tag Management Screen if activated
    if (showTagManagement) {
        TagManagementScreen(
            onNavigateBack = { 
                showTagManagement = false
                if (cameFromSettings) {
                    cameFromSettings = false
                    showSettings = true
                }
            },
            viewModel = viewModel
        )
        return
    }
    
    // Show Recently Deleted Screen if activated
    if (showRecentlyDeleted) {
        RecentlyDeletedScreen(
            onNavigateBack = { 
                showRecentlyDeleted = false
                if (cameFromSettings) {
                    cameFromSettings = false
                    showSettings = true
                }
            }
        )
        return
    }
    
    // Show Settings Screen if activated
    if (showSettings) {
        SettingsScreen(
            onNavigateBack = { showSettings = false },
            onExportBookmarks = { 
                showSettings = false
                showBackupRestore = true 
            },
            onImportBookmarks = { 
                showSettings = false
                showBackupRestore = true 
            },
            onTagManagement = {
                showSettings = false
                cameFromSettings = true
                showTagManagement = true
            },
            onRecentlyDeleted = {
                showSettings = false
                cameFromSettings = true
                showRecentlyDeleted = true
            },
            onReminders = {
                showSettings = false
                showReminders = true
            }
        )
        return
    }
    
    // Show Firebase Auth Screen if activated
    if (showFirebaseAuth) {
        FirebaseAuthScreen(
            onNavigateBack = { 
                showFirebaseAuth = false
                showBackupRestore = true
            },
            onAuthSuccess = {
                showFirebaseAuth = false
                showBackupRestore = true
            }
        )
        return
    }
    
    // Show Backup/Restore Screen if activated
    if (showBackupRestore) {
        BackupRestoreScreen(
            onNavigateBack = { 
                showBackupRestore = false
                showSettings = true 
            },
            onExportBookmarks = {
                showBackupRestore = false
                exportLauncher.launch("bookmarks_export.csv")
            },
            onImportBookmarks = {
                showBackupRestore = false
                importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values"))
            },
            onNavigateToAuth = {
                showBackupRestore = false
                showFirebaseAuth = true
            }
        )
        return
    }
    
    // Show Reminders Screen if activated
    if (showReminders) {
        RemindersScreen(
            onNavigateBack = { 
                showReminders = false
                showSettings = true 
            }
        )
        return
    }
    
    // Handle back gesture/button for search mode
    if (isSearchActive) {
        BackHandler {
            viewModel.setSearchActive(false)
        }
    }
    
    Scaffold(
        topBar = {
            if (isSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectedBookmarks.size,
                    onClearSelection = { viewModel.clearSelection() },
                    onSelectAll = { viewModel.selectAllBookmarks() },
                    onDelete = { viewModel.softDeleteSelectedBookmarks() },
                    onShare = { /* TODO: Implement share */ },
                    onMoveToFolder = { showFolderSelectionDialog = true }
                )
            } else if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearchClose = { viewModel.setSearchActive(false) }
                )
            } else {
                MainTopBar(
                    title = when (selectedTab) {
                        BookmarkTab.ALL -> stringResource(R.string.bookmarks)
                        BookmarkTab.FAVORITES -> stringResource(R.string.favorites)
                        BookmarkTab.READING_LIST -> stringResource(R.string.reading_list)
                        BookmarkTab.FOLDERS -> stringResource(R.string.folders)
                    },
                    onSearchClick = { viewModel.setSearchActive(true) },
                    onSortClick = { showSortMenu = true },
                    onViewModeClick = { showViewModeMenu = true },
                    onMoreClick = { showSettingsMenu = true },
                    showSortMenu = showSortMenu,
                    showViewModeMenu = showViewModeMenu,
                    showSettingsMenu = showSettingsMenu,
                    onSortDismiss = { showSortMenu = false },
                    onViewModeDismiss = { showViewModeMenu = false },
                    onSettingsDismiss = { showSettingsMenu = false },
                    onSortSelected = { sortOption ->
                        viewModel.setSortOption(sortOption)
                        showSortMenu = false
                    },
                    onViewModeSelected = { viewMode ->
                        viewModel.setViewMode(viewMode)
                        showViewModeMenu = false
                    },
                    onSettings = {
                        showSettings = true
                        showSettingsMenu = false
                    },
                    onAbout = {
                        showAboutDialog = true
                        showSettingsMenu = false
                    }
                )
            }
        },
        bottomBar = {
            if (!isSelectionMode && !isSearchActive) {
                BookmarkBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    favoriteCount = 0, // TODO: Get from ViewModel
                    readingListCount = 0 // TODO: Get from ViewModel
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode && !isSearchActive) {
                // Modern FAB with Material 3 Design
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == BookmarkTab.FOLDERS && currentFolder == null) {
                            showAddFolderDialog = true
                        } else {
                            showAddBookmarkDialog = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        if (selectedTab == BookmarkTab.FOLDERS) Icons.Default.CreateNewFolder else Icons.Default.Add,
                        contentDescription = if (selectedTab == BookmarkTab.FOLDERS) {
                            stringResource(R.string.add_folder)
                        } else {
                            stringResource(R.string.add_bookmark)
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                BookmarkTab.FOLDERS -> {
                    currentFolder?.let { folder ->
                        // Show folder contents
                        FolderContentView(
                            folder = folder,
                            viewModel = viewModel,
                            onNavigateBack = { currentFolder = null },
                            onAddBookmark = { showBookmarkSelectionDialog = true },
                            onEditBookmark = { bookmark -> editingBookmark = bookmark },
                            openBookmarkInBrowser = ::openBookmarkInBrowser,
                            openInWebView = ::openInWebView
                        )
                    } ?: run {
                        // Show folders list
                        if (folders.isEmpty()) {
                            EmptyState(
                                tab = selectedTab,
                                onAddBookmark = { showAddFolderDialog = true }
                            )
                        } else {
                            FolderList(
                                folders = folders,
                                onFolderClick = { folder -> currentFolder = folder },
                                onCreateFolder = { showAddFolderDialog = true },
                                onDeleteFolder = { folder ->
                                    viewModel.deleteFolder(folder)
                                    Toast.makeText(
                                        context,
                                        "Folder '${folder.name}' deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
                else -> {
                    Column {
                        // Tag Filter Bar (show when tags exist and search is active)
                        if (allTags.isNotEmpty() && !isSelectionMode && isSearchActive) {
                            TagFilterBar(
                                availableTags = allTags,
                                selectedTagIds = selectedTagFilters,
                                onTagSelected = { tagId -> viewModel.toggleTagFilter(tagId) },
                                onTagDeselected = { tagId -> viewModel.toggleTagFilter(tagId) },
                                onClearFilters = { viewModel.clearTagFilters() },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        // Bookmark List
                        if (bookmarksWithTags.isEmpty()) {
                            EmptyState(
                                tab = selectedTab,
                                onAddBookmark = { showAddBookmarkDialog = true }
                            )
                        } else {
                            BookmarkListWithTags(
                                bookmarksWithTags = bookmarksWithTags,
                                selectedBookmarks = selectedBookmarks,
                                isSelectionMode = isSelectionMode,
                                viewMode = viewMode,
                                onBookmarkClick = { bookmark ->
                                    if (isSelectionMode) {
                                        viewModel.toggleBookmarkSelection(bookmark.id)
                                    } else {
                                        // Open Reading List items in WebView, others in browser
                                        if (selectedTab == BookmarkTab.READING_LIST || bookmark.isInReadingList) {
                                            openInWebView(bookmark.url)
                                        } else {
                                            openBookmarkInBrowser(bookmark.url)
                                        }
                                        viewModel.incrementVisitCount(bookmark.id)
                                    }
                                },
                                onBookmarkLongClick = { bookmark ->
                                    if (!isSelectionMode) {
                                        // Show edit dialog on long press
                                        editingBookmark = bookmark
                                    }
                                },
                                onFavoriteToggle = { bookmark ->
                                    viewModel.toggleFavoriteStatus(bookmark.id, !bookmark.isFavorite)
                                },
                                onReadingListToggle = { bookmark ->
                                    viewModel.toggleReadingListStatus(bookmark.id, !bookmark.isInReadingList)
                                }
                            )
                        }
                    }
                }
            }
            
            // Undo Snackbar
            UndoSnackbar(
                undoAction = pendingUndoAction,
                onUndo = { viewModel.undoLastAction() },
                onDismiss = { viewModel.clearUndoAction() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    
    // Dialogs
    if (showAddBookmarkDialog) {
        AddBookmarkDialog(
            folders = folders,
            tags = viewModel.allTags.collectAsState().value,
            onDismiss = { showAddBookmarkDialog = false },
            onSave = { bookmark, tagIds ->
                // If we're in a folder view, set the bookmark's folder
                val currentFolderValue = currentFolder
                val bookmarkToSave = if (currentFolderValue != null) {
                    bookmark.copy(folderId = currentFolderValue.id)
                } else {
                    bookmark
                }
                viewModel.addBookmarkWithTags(bookmarkToSave, tagIds)
                showAddBookmarkDialog = false
            },
            onCreateTag = { tagName ->
                val newTag = viewModel.createTag(tagName)
                viewModel.addTag(newTag)
            },
            onGenerateTagSuggestions = { url, title ->
                viewModel.generateTagSuggestions(url, title)
            },
            initialTitle = sharedTitle ?: "",
            initialUrl = sharedUrl ?: ""
        )
    }
    
    if (showAddFolderDialog) {
        AddFolderDialog(
            onDismiss = { showAddFolderDialog = false },
            onSave = { folder ->
                viewModel.addFolder(folder)
                showAddFolderDialog = false
            }
        )
    }
    
    // Edit bookmark dialog
    editingBookmark?.let { bookmark ->
        val bookmarkWithTags = bookmarksWithTags.find { it.bookmark.id == bookmark.id }
        val bookmarkTags = bookmarkWithTags?.tags ?: emptyList()
        
        EditBookmarkDialog(
            bookmark = bookmark,
            folders = folders,
            tags = allTags,
            bookmarkTags = bookmarkTags,
            onDismiss = { editingBookmark = null },
            onSave = { updatedBookmark, tagIds ->
                viewModel.updateBookmark(updatedBookmark)
                viewModel.updateBookmarkTags(updatedBookmark.id, tagIds)
                editingBookmark = null
            },
            onDelete = {
                viewModel.softDeleteBookmark(bookmark)
                editingBookmark = null
            },
            onCreateTag = { tagName ->
                val newTag = viewModel.createTag(tagName)
                viewModel.addTag(newTag)
            }
        )
    }
    
    // Menus are now integrated into the TopBar
    
    // Folder selection dialog
    if (showFolderSelectionDialog) {
        FolderSelectionDialog(
            folders = folders,
            selectedFolderId = null,
            onDismiss = { showFolderSelectionDialog = false },
            onFolderSelected = { selectedFolder ->
                val selectedBookmarkIds = selectedBookmarks.toList()
                viewModel.moveBookmarksToFolder(selectedBookmarkIds, selectedFolder?.id)
                viewModel.clearSelection()
                showFolderSelectionDialog = false
                
                // Show confirmation message
                val folderName = selectedFolder?.name ?: "No Folder"
                val count = selectedBookmarkIds.size
                Toast.makeText(
                    context, 
                    "$count bookmark${if (count > 1) "s" else ""} moved to $folderName", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
    
    // Bookmark selection dialog for adding existing bookmarks to folders
    if (showBookmarkSelectionDialog) {
        currentFolder?.let { folder ->
            BookmarkSelectionDialog(
                bookmarks = currentBookmarks,
                currentFolderId = folder.id,
                onDismiss = { showBookmarkSelectionDialog = false },
                onBookmarksSelected = { selectedBookmarks ->
                    // Move selected bookmarks to the current folder
                    val bookmarkIds = selectedBookmarks.map { it.id }
                    viewModel.moveBookmarksToFolder(bookmarkIds, folder.id)
                    showBookmarkSelectionDialog = false
                    
                    // Show confirmation message
                    val count = bookmarkIds.size
                    Toast.makeText(
                        context,
                        "$count bookmark${if (count > 1) "s" else ""} added to ${folder.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                title = "Add Bookmarks to ${currentFolder?.name ?: "Folder"}"
            )
        }
    }
    
    // Permission dialog
    PermissionDialog(
        showDialog = showPermissionDialog,
        permissionManager = permissionManager,
        onDismiss = {
            showPermissionDialog = false
        }
    )
    
    // About dialog
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            onCheckForUpdates = {
                updateCheckerViewModel.forceCheckForUpdates()
                Toast.makeText(context, "Checking for updates...", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    // Update dialog
    when (val state = updateState) {
        is UpdateCheckerViewModel.UpdateState.UpdateAvailable -> {
            UpdateDialog(
                currentVersion = state.updateInfo.currentVersion,
                latestVersion = state.updateInfo.latestVersion ?: "Unknown",
                releaseNotes = state.updateInfo.releaseNotes,
                downloadUrl = state.updateInfo.downloadUrl ?: "",
                onUpdateClicked = {
                    updateCheckerViewModel.hideUpdateDialog()
                },
                onDismiss = {
                    updateCheckerViewModel.hideUpdateDialog()
                },
                onNotNow = {
                    updateCheckerViewModel.dismissUpdate()
                }
            )
        }
        is UpdateCheckerViewModel.UpdateState.NoUpdateFound -> {
            LaunchedEffect(state) {
                Toast.makeText(context, "You are on current version", Toast.LENGTH_SHORT).show()
                updateCheckerViewModel.hideUpdateDialog()
            }
        }
        is UpdateCheckerViewModel.UpdateState.Error -> {
            LaunchedEffect(state) {
                Toast.makeText(context, "Update check failed: ${state.message}", Toast.LENGTH_LONG).show()
                updateCheckerViewModel.hideUpdateDialog()
            }
        }
        else -> { /* No dialog needed */ }
    }
}
