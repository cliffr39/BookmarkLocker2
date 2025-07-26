package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import android.widget.Toast
import com.bookmark.locker.R
import com.bookmark.locker.permission.PermissionManager

@Composable
fun PermissionDialog(
    showDialog: Boolean,
    permissionManager: PermissionManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Get permission states
    val hasNotificationPermission by permissionManager.hasNotificationPermission.collectAsState()
    val hasAlarmPermission by permissionManager.hasExactAlarmPermission.collectAsState()
    
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Permissions Required",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "To use reminders, this app needs the following permissions:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Notification permission (Android 13+)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        PermissionItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            description = "Show reminder notifications",
                            isGranted = hasNotificationPermission,
                            onRequest = {
                                permissionManager.requestAllPermissions { granted ->
                                    if (granted) {
                                        Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Exact alarm permission (Android 12+)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        PermissionItem(
                            icon = Icons.Default.Schedule,
                            title = "Exact Alarms",
                            description = "Schedule precise reminders",
                            isGranted = hasAlarmPermission,
                            onRequest = {
                                permissionManager.requestAllPermissions { granted ->
                                    if (granted) {
                                        Toast.makeText(context, "Alarm permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Skip")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                // Request all missing permissions
                                permissionManager.requestAllPermissions { granted ->
                                    if (granted) {
                                        Toast.makeText(context, "All permissions granted!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "Some permissions were not granted", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Text("Grant Permissions")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        if (isGranted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Granted",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            FilledTonalButton(
                onClick = onRequest,
                modifier = Modifier.size(width = 80.dp, height = 32.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "Enable",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
