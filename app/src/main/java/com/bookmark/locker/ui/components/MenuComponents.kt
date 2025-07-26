package com.bookmark.locker.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bookmark.locker.ui.viewmodel.SortOption
import com.bookmark.locker.ui.viewmodel.ViewMode

@Composable
fun SortMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSortSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        SortOption.values().forEach { option ->
            DropdownMenuItem(
                text = { Text(text = option.name) },
                onClick = { 
                    onSortSelected(option)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun ViewModeMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onViewModeSelected: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        ViewMode.values().forEach { mode ->
            DropdownMenuItem(
                text = { Text(text = mode.name) },
                onClick = { 
                    onViewModeSelected(mode)
                    onDismiss()
                }
            )
        }
    }
}
