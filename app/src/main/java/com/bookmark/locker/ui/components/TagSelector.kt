package com.bookmark.locker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bookmark.locker.data.model.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelector(
    availableTags: List<Tag>,
    selectedTags: List<Tag>,
    onTagSelected: (Tag) -> Unit,
    onTagRemoved: (Tag) -> Unit,
    onCreateTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTagName by remember { mutableStateOf("") }
    var showNewTagField by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Selected tags section
        if (selectedTags.isNotEmpty()) {
            Text(
                text = "Selected Tags",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(selectedTags) { tag ->
                    FilterChip(
                        onClick = { onTagRemoved(tag) },
                        label = { Text(tag.name) },
                        selected = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove tag",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }

        // Available tags section
        val unselectedTags = availableTags.filter { available ->
            selectedTags.none { selected -> selected.id == available.id }
        }

        if (unselectedTags.isNotEmpty()) {
            Text(
                text = "Available Tags",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(unselectedTags) { tag ->
                    FilterChip(
                        onClick = { onTagSelected(tag) },
                        label = { Text(tag.name) },
                        selected = false
                    )
                }
            }
        }

        // Add new tag section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showNewTagField) {
                OutlinedTextField(
                    value = newTagName,
                    onValueChange = { newTagName = it },
                    label = { Text("New tag name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newTagName.isNotBlank()) {
                                onCreateTag(newTagName.trim())
                                newTagName = ""
                                showNewTagField = false
                            }
                        }
                    )
                )
                
                TextButton(
                    onClick = { 
                        showNewTagField = false
                        newTagName = ""
                    }
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            onCreateTag(newTagName.trim())
                            newTagName = ""
                            showNewTagField = false
                        }
                    },
                    enabled = newTagName.isNotBlank()
                ) {
                    Text("Add")
                }
            } else {
                OutlinedButton(
                    onClick = { showNewTagField = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add new tag",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Tag")
                }
            }
        }
    }
}

@Composable
fun TagChip(
    tag: Tag,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = { Text(tag.name) },
        selected = isSelected,
        modifier = modifier,
        trailingIcon = if (isSelected && onRemove != null) {
            {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove tag",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        } else null
    )
}

@Composable
fun SuggestedTags(
    suggestedTags: List<String>,
    onTagSelected: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (suggestedTags.isNotEmpty() || isLoading) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Suggested Tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.dp
                    )
                }
            }
            
            if (!isLoading && suggestedTags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(suggestedTags) { tagName ->
                        SuggestedTagChip(
                            tagName = tagName,
                            onClick = { onTagSelected(tagName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedTagChip(
    tagName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add suggested tag",
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tagName,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun TagColorPicker(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Red.hashCode(),
        Color.Green.hashCode(), 
        Color.Blue.hashCode(),
        Color.Yellow.hashCode(),
        Color.Magenta.hashCode(),
        Color.Cyan.hashCode(),
        Color(0xFF9C27B0).hashCode(), // Purple
        Color(0xFF4CAF50).hashCode(), // Green
        Color(0xFF2196F3).hashCode(), // Blue
        Color(0xFFFF9800).hashCode(), // Orange
        Color(0xFF607D8B).hashCode(), // Blue Grey
        Color(0xFF795548).hashCode()  // Brown
    )
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(colors) { color ->
            ColorPickerItem(
                color = Color(color),
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorPickerItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
