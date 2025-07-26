package com.bookmark.locker.permission

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(private val activity: ComponentActivity) {
    
    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()
    
    private val _hasExactAlarmPermission = MutableStateFlow(false)
    val hasExactAlarmPermission: StateFlow<Boolean> = _hasExactAlarmPermission.asStateFlow()
    
    private var onPermissionResult: ((Boolean) -> Unit)? = null
    
    // Notification permission launcher (Android 13+)
    private val notificationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        _hasNotificationPermission.value = granted
        Log.d("PermissionManager", "Notification permission result: $granted")
        
        // After notification permission, check if we need alarm permission
        if (granted) {
            checkAndRequestAlarmPermission()
        } else {
            onPermissionResult?.invoke(false)
            onPermissionResult = null
        }
    }
    
    // Alarm permission launcher (Android 12+)
    private val alarmPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val hasAlarmPermission = checkExactAlarmPermission()
        _hasExactAlarmPermission.value = hasAlarmPermission
        Log.d("PermissionManager", "Alarm permission result: $hasAlarmPermission")
        
        onPermissionResult?.invoke(hasAlarmPermission)
        onPermissionResult = null
    }
    
    init {
        // Initialize permission states
        _hasNotificationPermission.value = checkNotificationPermission()
        _hasExactAlarmPermission.value = checkExactAlarmPermission()
    }
    
    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            Log.d("PermissionManager", "Notification permission check: $granted")
            granted
        } else {
            Log.d("PermissionManager", "Android < 13, notification permission not required")
            true
        }
    }
    
    private fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val granted = alarmManager.canScheduleExactAlarms()
            Log.d("PermissionManager", "Exact alarm permission check: $granted")
            granted
        } else {
            Log.d("PermissionManager", "Android < 12, exact alarm permission not required")
            true
        }
    }
    
    fun requestAllPermissions(onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult
        
        Log.d("PermissionManager", "Requesting all permissions...")
        Log.d("PermissionManager", "Has notification: ${_hasNotificationPermission.value}")
        Log.d("PermissionManager", "Has alarm: ${_hasExactAlarmPermission.value}")
        
        // Check notification permission first (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !checkNotificationPermission()) {
            Log.d("PermissionManager", "Requesting notification permission")
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // If notification permission is not needed or already granted, check alarm permission
            checkAndRequestAlarmPermission()
        }
    }
    
    private fun checkAndRequestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !checkExactAlarmPermission()) {
            Log.d("PermissionManager", "Requesting exact alarm permission")
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                alarmPermissionLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e("PermissionManager", "Failed to request alarm permission", e)
                onPermissionResult?.invoke(false)
                onPermissionResult = null
            }
        } else {
            // All permissions are granted or not needed
            val allGranted = checkNotificationPermission() && checkExactAlarmPermission()
            Log.d("PermissionManager", "All permissions check result: $allGranted")
            onPermissionResult?.invoke(allGranted)
            onPermissionResult = null
        }
    }
    
    fun refreshPermissionStates() {
        _hasNotificationPermission.value = checkNotificationPermission()
        _hasExactAlarmPermission.value = checkExactAlarmPermission()
    }
    
    fun hasAllPermissions(): Boolean {
        return checkNotificationPermission() && checkExactAlarmPermission()
    }
}
