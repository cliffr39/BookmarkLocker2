package com.bookmark.locker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.ui.components.EmptyState
import com.bookmark.locker.ui.viewmodel.RecentlyDeletedViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyDeletedScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecentlyDeletedViewModel = hiltViewModel()
) {
    val deletedBookmarks by viewModel.deletedBookmarks.collectAsState(emptyList())
    val isLoading by viewModel.isLoading.collectAsState(false)
    val selectedBookmarks by viewModel.selectedBookmarks.collectAsState(emptySet())
    val isSelectionMode = selectedBookmarks.isNotEmpty()
    
    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showPermanentDeleteDialog by remember { mutableStateOf(false) }
    
    // Handle back gesture
    BackHandler {
        if (isSelectionMode) {
            viewModel.clearSelection()
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isSelectionMode) {
                            "${selectedBookmarks.size} selected"
                        } else {
                            "Recently Deleted"
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) {
                            viewModel.clearSelection()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = if (isSelectionMode) Icons.Default.Close else Icons.Default.ArrowBack,
                            contentDescription = if (isSelectionMode) "Clear selection" else "Back"
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { showRestoreDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Restore selected"
                            )
                        }
                        IconButton(onClick = { showPermanentDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete permanently"
                            )
                        }
                    } else if (deletedBookmarks.isNotEmpty()) {
                        IconButton(onClick = { showEmptyTrashDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Empty trash"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (deletedBookmarks.isEmpty()) {
                SimpleEmptyState(
                    icon = Icons.Default.DeleteOutline,
                    title = "No deleted bookmarks",
                    description = "When you delete bookmarks, they'll appear here for 30 days before being permanently removed.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Deleted bookmarks will be permanently removed after 30 days",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    
                    items(
                        items = deletedBookmarks,
                        key = { bookmark: Bookmark -> bookmark.id }
                    ) { bookmark: Bookmark ->
                        DeletedBookmarkItem(
                            bookmark = bookmark,
                            isSelected = selectedBookmarks.contains(bookmark.id),
                            isSelectionMode = isSelectionMode,
                            onToggleSelection = { viewModel.toggleSelection(bookmark.id) },
                            onLongClick = { viewModel.toggleSelection(bookmark.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = { Text("Empty trash?") },
            text = { Text("This will permanently delete all bookmarks in the trash. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.emptyTrash()
                        showEmptyTrashDialog = false
                    }
                ) {
                    Text("Empty")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEmptyTrashDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore bookmarks?") },
            text = { 
                Text("${selectedBookmarks.size} bookmark(s) will be restored to your bookmarks.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.restoreSelectedBookmarks()
                        showRestoreDialog = false
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestoreDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPermanentDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showPermanentDeleteDialog = false },
            title = { Text("Delete permanently?") },
            text = { 
                Text("${selectedBookmarks.size} bookmark(s) will be permanently deleted. This action cannot be undone.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.permanentlyDeleteSelectedBookmarks()
                        showPermanentDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermanentDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeletedBookmarkItem(
    bookmark: Bookmark,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggleSelection: () -> Unit,
    onLongClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val deletedDate = bookmark.deletedAt?.let { dateFormat.format(Date(it)) } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelectionMode) {
                    Modifier
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = {
            if (isSelectionMode) {
                onToggleSelection()
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                if (deletedDate.isNotEmpty()) {
                    Text(
                        text = "Deleted on $deletedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            if (!isSelectionMode) {
                IconButton(onClick = onLongClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
