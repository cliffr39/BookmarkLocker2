package com.bookmark.locker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bookmark.locker.data.model.Tag

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
fun TagFilterBar(
    availableTags: List<Tag>,
    selectedTagIds: Set<Long>,
    onTagSelected: (Long) -> Unit,
    onTagDeselected: (Long) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTagSelector by remember { mutableStateOf(false) }
    val selectedTags = availableTags.filter { it.id in selectedTagIds }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with filter icon and clear button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter by tags",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (selectedTags.isEmpty()) "Filter by tags" else "Filtered by ${selectedTags.size} tag${if (selectedTags.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (selectedTags.isNotEmpty()) {
                    TextButton(
                        onClick = onClearFilters,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear filters",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear")
                    }
                }
            }
            
            // Selected tags row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Add tag button
                item {
                    OutlinedButton(
                        onClick = { showTagSelector = true },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add filter",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Tag", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                // Selected tag chips
                items(selectedTags) { tag ->
                    FilterTagChip(
                        tag = tag,
                        onRemove = { onTagDeselected(tag.id) }
                    )
                }
            }
        }
    }
    
    // Tag selector dialog
    if (showTagSelector) {
        TagSelectorDialog(
            availableTags = availableTags,
            selectedTagIds = selectedTagIds,
            onTagToggle = { tagId ->
                if (tagId in selectedTagIds) {
                    onTagDeselected(tagId)
                } else {
                    onTagSelected(tagId)
                }
            },
            onDismiss = { showTagSelector = false }
        )
    }
}

@Composable
private fun FilterTagChip(
    tag: Tag,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onRemove,
        label = { 
            Text(
                text = tag.name,
                style = MaterialTheme.typography.bodySmall
            )
        },
        selected = true,
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove filter",
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = parseTagColor(tag.color).copy(alpha = 0.2f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderColor = parseTagColor(tag.color)
        )
    )
}

@Composable
private fun TagSelectorDialog(
    availableTags: List<Tag>,
    selectedTagIds: Set<Long>,
    onTagToggle: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Select Tags to Filter")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(availableTags.sortedBy { it.name }) { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tag.id in selectedTagIds,
                            onCheckedChange = { onTagToggle(tag.id) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = parseTagColor(tag.color),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
