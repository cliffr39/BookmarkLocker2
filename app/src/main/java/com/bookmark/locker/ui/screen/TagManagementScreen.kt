package com.bookmark.locker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.BackHandler
import com.bookmark.locker.R
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.ui.viewmodel.BookmarkViewModel
import com.bookmark.locker.ui.components.TagColorPicker

// Helper function to safely parse tag colors
private fun parseTagColor(colorString: String?): Color {
    return try {
        if (colorString.isNullOrBlank()) {
            Color(0xFF2196F3) // Default blue
        } else {
            when {
                colorString.startsWith("0x") || colorString.startsWith("0X") -> {
                    Color(colorString.substring(2).toLong(16))
                }
                colorString.startsWith("#") -> {
                    Color(colorString.substring(1).toLong(16))
                }
                else -> {
                    Color(colorString.toLong())
                }
            }
        }
    } catch (e: Exception) {
        Color(0xFF2196F3) // Default blue on any parsing error
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val tags by viewModel.allTags.collectAsState()
    var showAddTagDialog by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Tag?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Tag?>(null) }
    
    // Handle back gesture
    BackHandler {
        onNavigateBack()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Tag Management",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddTagDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Tag"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (tags.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Label,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No tags created yet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Create tags to organize your bookmarks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Button(
                        onClick = { showAddTagDialog = true },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Tag")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags.sortedBy { it.name }) { tag ->
                    TagManagementItem(
                        tag = tag,
                        onEdit = { editingTag = tag },
                        onDelete = { showDeleteConfirmDialog = tag }
                    )
                }
            }
        }
    }
    
    // Add Tag Dialog
    if (showAddTagDialog) {
        AddTagDialog(
            onDismiss = { showAddTagDialog = false },
            onSave = { tagName, tagColor ->
                val newTag = Tag(
                    name = tagName,
                    color = tagColor.toString(),
                    dateCreated = System.currentTimeMillis(),
                    usageCount = 0
                )
                viewModel.addTag(newTag)
                showAddTagDialog = false
            }
        )
    }
    
    // Edit Tag Dialog
    editingTag?.let { tag ->
        EditTagDialog(
            tag = tag,
            onDismiss = { editingTag = null },
            onSave = { updatedTag ->
                viewModel.updateTag(updatedTag)
                editingTag = null
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteConfirmDialog?.let { tag ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Tag") },
            text = { 
                Text("Are you sure you want to delete \"${tag.name}\"? This will remove the tag from all bookmarks.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTag(tag)
                        showDeleteConfirmDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TagManagementItem(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag color indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .fillMaxHeight()
                    .background(
                        color = parseTagColor(tag.color),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Tag info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${tag.usageCount} bookmarks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Action buttons
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit tag",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete tag",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddTagDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Blue.hashCode()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Tag") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium
                )
                
                TagColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (tagName.isNotBlank()) {
                        onSave(tagName.trim(), selectedColor)
                    }
                },
                enabled = tagName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditTagDialog(
    tag: Tag,
    onDismiss: () -> Unit,
    onSave: (Tag) -> Unit
) {
    var tagName by remember { mutableStateOf(tag.name) }
    var selectedColor by remember { mutableStateOf(tag.color?.toIntOrNull() ?: Color.Blue.hashCode()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tag") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium
                )
                
                TagColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (tagName.isNotBlank()) {
                        onSave(tag.copy(name = tagName.trim(), color = selectedColor.toString()))
                    }
                },
                enabled = tagName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
