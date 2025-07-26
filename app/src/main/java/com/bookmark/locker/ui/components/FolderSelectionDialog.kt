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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bookmark.locker.data.model.Folder

@Composable
fun FolderSelectionDialog(
    folders: List<Folder>,
    selectedFolderId: Long?,
    onDismiss: () -> Unit,
    onFolderSelected: (Folder?) -> Unit,
    title: String = "Move to Folder",
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Dialog title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Folder list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Option to remove from folder (no folder)
                    item {
                        FolderOption(
                            folder = null,
                            isSelected = selectedFolderId == null,
                            onSelect = { onFolderSelected(null) }
                        )
                    }
                    
                    // List of available folders
                    items(folders) { folder ->
                        FolderOption(
                            folder = folder,
                            isSelected = selectedFolderId == folder.id,
                            onSelect = { onFolderSelected(folder) }
                        )
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
                }
            }
        }
    }
}

@Composable
private fun FolderOption(
    folder: Folder?,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Icon(
            imageVector = if (folder == null) Icons.Default.FolderOff else Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (folder == null) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = folder?.name ?: "No Folder",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
