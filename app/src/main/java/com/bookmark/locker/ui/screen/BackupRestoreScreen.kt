package com.bookmark.locker.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bookmark.locker.ui.viewmodel.AuthState
import com.bookmark.locker.ui.viewmodel.BackupState
import com.bookmark.locker.ui.viewmodel.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onNavigateBack: () -> Unit,
    onExportBookmarks: () -> Unit,
    onImportBookmarks: () -> Unit,
    onNavigateToAuth: () -> Unit,
    firebaseViewModel: FirebaseViewModel = hiltViewModel()
) {
    val authState by firebaseViewModel.authState.collectAsState()
    val backupState by firebaseViewModel.backupState.collectAsState()
    
    // Show snackbar for backup operations
    LaunchedEffect(backupState) {
        if (backupState is BackupState.Success || backupState is BackupState.Error) {
            // Clear the state after showing the message
        }
    }
    
    // Handle system back gesture
    BackHandler {
        onNavigateBack()
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Backup / Restore") },
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
            // Export Section
            item {
                Text(
                    text = "Export",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                Card(
                    onClick = onExportBookmarks,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    ListItem(
                        headlineContent = { Text("Export Bookmarks") },
                        supportingContent = { 
                            Text("Save all your bookmarks to a CSV file that you can backup or share")
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.FileUpload,
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
            
            // Import Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Import",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                Card(
                    onClick = onImportBookmarks,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    ListItem(
                        headlineContent = { Text("Import Bookmarks") },
                        supportingContent = { 
                            Text("Load bookmarks from a CSV file. New bookmarks will be added to your collection")
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.FileDownload,
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
            
            // Cloud Backup Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cloud Backup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            when (authState) {
                is AuthState.SignedOut -> {
                    item {
                        Card(
                            onClick = onNavigateToAuth,
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Sign In for Cloud Backup") },
                                supportingContent = {
                                    Text("Sign in to backup your bookmarks to the cloud and sync across devices")
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.CloudUpload,
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
                }
                is AuthState.SignedIn -> {
                    item {
                        Card(
                            onClick = { firebaseViewModel.backupBookmarks() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Backup to Cloud") },
                                supportingContent = {
                                    Text("Upload your bookmarks to Firebase cloud storage")
                                },
                                leadingContent = {
                                    if (backupState is BackupState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp),
                                            strokeWidth = 3.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
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
                    
                    item {
                        Card(
                            onClick = { firebaseViewModel.restoreBookmarks() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Restore from Cloud") },
                                supportingContent = {
                                    Text("Download and restore your bookmarks from Firebase cloud storage")
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.CloudDownload,
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
                    
                    item {
                        OutlinedCard(
                            onClick = { firebaseViewModel.signOut() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                headlineContent = { Text("Sign Out") },
                                supportingContent = {
                                    Text("Signed in as: ${(authState as AuthState.SignedIn).user.email ?: "Anonymous"}")
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            )
                        }
                    }
                }
                is AuthState.Loading -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Signing In...") },
                                leadingContent = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            )
                        }
                    }
                }
                is AuthState.Error -> {
                    item {
                        Card(
                            onClick = onNavigateToAuth,
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Authentication Error") },
                                supportingContent = {
                                    Text((authState as AuthState.Error).message)
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
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
                }
            }
            
            // Show backup state messages
            when (backupState) {
                is BackupState.Success -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Success") },
                                supportingContent = { Text((backupState as BackupState.Success).message) },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { firebaseViewModel.clearBackupState() }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Dismiss"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                is BackupState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            ListItem(
                                headlineContent = { Text("Error") },
                                supportingContent = { Text((backupState as BackupState.Error).message) },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { firebaseViewModel.clearBackupState() }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Dismiss"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                else -> {}
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
                                text = "About CSV Format",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Exported files contain your bookmark titles, URLs, tags, and folder information in CSV format. This format is compatible with most spreadsheet applications and other bookmark managers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
