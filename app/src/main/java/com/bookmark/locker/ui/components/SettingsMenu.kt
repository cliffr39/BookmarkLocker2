package com.bookmark.locker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bookmark.locker.R

@Composable
fun SettingsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text("Settings") },
            leadingIcon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null
                )
            },
            onClick = {
                onSettings()
                onDismiss()
            }
        )
        
        DropdownMenuItem(
            text = { Text("About") },
            leadingIcon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null
                )
            },
            onClick = {
                onAbout()
                onDismiss()
            }
        )
    }
}
