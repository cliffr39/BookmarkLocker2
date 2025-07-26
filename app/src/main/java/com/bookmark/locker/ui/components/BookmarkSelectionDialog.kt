package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bookmark.locker.data.model.Bookmark

@Composable
fun BookmarkSelectionDialog(
    bookmarks: List<Bookmark>,
    currentFolderId: Long?,
    onDismiss: () -> Unit,
    onBookmarksSelected: (List<Bookmark>) -> Unit,
    title: String = "Add Bookmarks to Folder",
    modifier: Modifier = Modifier
) {
    var selectedBookmarks by remember { mutableStateOf(setOf<Long>()) }
    
    // Filter out bookmarks that are already in the current folder
    val availableBookmarks = bookmarks.filter { it.folderId != currentFolderId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Dialog title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (availableBookmarks.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
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
                                text = "No bookmarks available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "All bookmarks are already in this folder or other locations",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    // Selection info
                    Text(
                        text = if (selectedBookmarks.isEmpty()) {
                            "Select bookmarks to add to this folder"
                        } else {
                            "${selectedBookmarks.size} bookmark${if (selectedBookmarks.size != 1) "s" else ""} selected"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Bookmarks list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableBookmarks) { bookmark ->
                            BookmarkSelectionItem(
                                bookmark = bookmark,
                                isSelected = selectedBookmarks.contains(bookmark.id),
                                onSelectionChange = { selected ->
                                    selectedBookmarks = if (selected) {
                                        selectedBookmarks + bookmark.id
                                    } else {
                                        selectedBookmarks - bookmark.id
                                    }
                                }
                            )
                        }
                    }
                }
                
                // Dialog buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val bookmarksToMove = availableBookmarks.filter { 
                                selectedBookmarks.contains(it.id) 
                            }
                            onBookmarksSelected(bookmarksToMove)
                        },
                        enabled = selectedBookmarks.isNotEmpty()
                    ) {
                        Text("Add to Folder")
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkSelectionItem(
    bookmark: Bookmark,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = { onSelectionChange(!isSelected) },
                role = Role.Checkbox
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChange
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bookmark.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = bookmark.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Show current folder if any
            bookmark.folderId?.let { folderId ->
                Text(
                    text = "Currently in folder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
