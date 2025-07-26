package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bookmark.locker.R
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.ui.viewmodel.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderContentView(
    folder: Folder,
    viewModel: BookmarkViewModel,
    onNavigateBack: () -> Unit,
    onAddBookmark: () -> Unit,
    onEditBookmark: (Bookmark) -> Unit,
    openBookmarkInBrowser: (String) -> Unit,
    openInWebView: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get bookmarks for this folder
    val folderBookmarks by viewModel.getBookmarksByFolder(folder.id).collectAsState(initial = emptyList())
    val selectedBookmarks by viewModel.selectedBookmarks.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    
    Column(modifier = modifier.fillMaxSize()) {
        // Folder header with back button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to folders",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${folderBookmarks.size} bookmark${if (folderBookmarks.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                // Add bookmark to folder button
                IconButton(
                    onClick = onAddBookmark,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add bookmark to folder",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // Bookmarks list
        if (folderBookmarks.isEmpty()) {
            // Empty state for folder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    
                    Text(
                        text = "No bookmarks in this folder",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Add your first bookmark to this folder",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    
                    Button(
                        onClick = onAddBookmark,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Bookmark")
                    }
                }
            }
        } else {
            // Show bookmarks in this folder
            BookmarkList(
                bookmarks = folderBookmarks,
                selectedBookmarks = selectedBookmarks,
                isSelectionMode = isSelectionMode,
                viewMode = viewMode,
                onBookmarkClick = { bookmark ->
                    if (isSelectionMode) {
                        viewModel.toggleBookmarkSelection(bookmark.id)
                    } else {
                        // Open bookmark based on type
                        if (bookmark.isInReadingList) {
                            openInWebView(bookmark.url)
                        } else {
                            openBookmarkInBrowser(bookmark.url)
                        }
                        viewModel.incrementVisitCount(bookmark.id)
                    }
                },
                onBookmarkLongClick = { bookmark ->
                    if (!isSelectionMode) {
                        onEditBookmark(bookmark)
                    }
                },
                onFavoriteToggle = { bookmark ->
                    viewModel.toggleFavoriteStatus(bookmark.id, !bookmark.isFavorite)
                },
                onReadingListToggle = { bookmark ->
                    viewModel.toggleReadingListStatus(bookmark.id, !bookmark.isInReadingList)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
