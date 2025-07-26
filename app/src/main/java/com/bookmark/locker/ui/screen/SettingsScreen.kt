package com.bookmark.locker.ui.screen

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bookmark.locker.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onExportBookmarks: () -> Unit,
    onImportBookmarks: () -> Unit,
    onTagManagement: () -> Unit,
    onRecentlyDeleted: () -> Unit,
    onReminders: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    // Handle system back gesture
    BackHandler {
        onNavigateBack()
    }
    val context = LocalContext.current
    val selectedRingtoneUri by settingsViewModel.selectedRingtoneUri.collectAsState()
    val selectedRingtoneName by settingsViewModel.selectedRingtoneName.collectAsState()
    val snoozeDuration by settingsViewModel.snoozeDuration.collectAsState()
    
    var showSnoozeDurationDialog by remember { mutableStateOf(false) }
    
    // Ringtone picker launcher
    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            settingsViewModel.setRingtoneUri(uri)
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Backup / Restore Section
            item {
                SettingsSection(
                    title = "Backup / Restore",
                    icon = Icons.Default.CloudSync,
                    description = "Export and import your bookmarks",
                    onClick = onExportBookmarks
                )
            }
            
            // Reminders Section
            item {
                SettingsSection(
                    title = "Reminders",
                    icon = Icons.Default.NotificationsActive,
                    description = "Notification sounds and snooze settings",
                    onClick = onReminders
                )
            }
            
            // Recently Deleted Section
            item {
                SettingsSection(
                    title = "Recently Deleted",
                    icon = Icons.Default.DeleteOutline,
                    description = "View and restore deleted bookmarks",
                    onClick = onRecentlyDeleted
                )
            }
            
            // Tag Management Section
            item {
                SettingsSection(
                    title = "Tag Management",
                    icon = Icons.Default.Label,
                    description = "Organize and manage your tags",
                    onClick = onTagManagement
                )
            }
        }
    }
    
    // Snooze Duration Selection Dialog
    if (showSnoozeDurationDialog) {
        SnoozeDurationDialog(
            currentDuration = snoozeDuration,
            onDismiss = { showSnoozeDurationDialog = false },
            onDurationSelected = { duration ->
                settingsViewModel.setSnoozeDuration(duration)
                showSnoozeDurationDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            supportingContent = { 
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            trailingContent = {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun SnoozeDurationDialog(
    currentDuration: Long,
    onDismiss: () -> Unit,
    onDurationSelected: (Long) -> Unit
) {
    val snoozeDurations = listOf(
        5 * 60 * 1000L to "5 minutes",
        15 * 60 * 1000L to "15 minutes", 
        30 * 60 * 1000L to "30 minutes",
        60 * 60 * 1000L to "1 hour",
        2 * 60 * 60 * 1000L to "2 hours",
        4 * 60 * 60 * 1000L to "4 hours"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Snooze Duration") },
        text = {
            LazyColumn {
                items(snoozeDurations.size) { index ->
                    val (duration, label) = snoozeDurations[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentDuration == duration,
                            onClick = { onDurationSelected(duration) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatSnoozeDuration(durationMs: Long): String {
    return when (durationMs) {
        5 * 60 * 1000L -> "5 minutes"
        15 * 60 * 1000L -> "15 minutes"
        30 * 60 * 1000L -> "30 minutes"
        60 * 60 * 1000L -> "1 hour"
        2 * 60 * 60 * 1000L -> "2 hours"
        4 * 60 * 60 * 1000L -> "4 hours"
        else -> "${durationMs / (60 * 1000)} minutes"
    }
}
