package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bookmark.locker.data.model.UndoAction
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UndoSnackbar(
    undoAction: UndoAction?,
    onUndo: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoHideDuration: Long = 5000L // 5 seconds
) {
    undoAction?.let { action ->
        LaunchedEffect(action) {
            delay(autoHideDuration)
            onDismiss()
        }
        
        Snackbar(
            modifier = modifier,
            action = {
                TextButton(
                    onClick = {
                        onUndo()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "UNDO",
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            },
            dismissAction = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "✕",
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface
        ) {
            Text(text = action.message)
        }
    }
}

@Composable
fun UndoSnackbarHost(
    undoAction: UndoAction?,
    onUndo: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (undoAction != null) {
        UndoSnackbar(
            undoAction = undoAction,
            onUndo = onUndo,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}
