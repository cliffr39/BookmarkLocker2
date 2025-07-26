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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bookmark.locker.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    // Handle system back gesture
    BackHandler {
        onNavigateBack()
    }
    
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
                title = { Text("Reminders") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications Section
            item {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Ringtone Selection
            item {
                Card(
                    onClick = {
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Reminder Sound")
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                        }
                        ringtonePickerLauncher.launch(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    ListItem(
                        headlineContent = { Text("Reminder Sound") },
                        supportingContent = { 
                            Text(selectedRingtoneName.ifEmpty { "Default notification sound" })
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            
            // Snooze Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Snooze Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Snooze Duration
            item {
                Card(
                    onClick = { showSnoozeDurationDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    ListItem(
                        headlineContent = { Text("Default Snooze Duration") },
                        supportingContent = { 
                            Text(formatSnoozeDuration(snoozeDuration))
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.Snooze,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            
            // Info Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "About Reminders",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reminders help you remember to read bookmarks you've saved. You can set custom notification sounds and adjust how long the snooze function delays the reminder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
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
