package com.bookmark.locker.permission

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

class AlarmPermissionManager(private val activity: ComponentActivity) {
    
    private val _hasExactAlarmPermission = MutableStateFlow(false)
    val hasExactAlarmPermission: StateFlow<Boolean> = _hasExactAlarmPermission.asStateFlow()
    
    private var onPermissionResult: ((Boolean) -> Unit)? = null
    
    private val exactAlarmPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check permission after returning from settings
        val hasPermission = checkExactAlarmPermission()
        _hasExactAlarmPermission.value = hasPermission
        onPermissionResult?.invoke(hasPermission)
        onPermissionResult = null
    }
    
    init {
        // Initialize permission state
        _hasExactAlarmPermission.value = checkExactAlarmPermission()
    }
    
    fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val hasPermission = alarmManager.canScheduleExactAlarms()
            Log.d("AlarmPermissionManager", "Android API ${Build.VERSION.SDK_INT}, Exact alarm permission: $hasPermission")
            hasPermission
        } else {
            Log.d("AlarmPermissionManager", "Android < 12, no exact alarm permission needed")
            true
        }
    }
    
    fun requestExactAlarmPermission(onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkExactAlarmPermission()) {
                _hasExactAlarmPermission.value = true
                onResult(true)
                return
            }
            
            onPermissionResult = onResult
            
            try {
                // Try the specific exact alarm settings first
                val exactAlarmIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                Log.d("AlarmPermissionManager", "Attempting to request exact alarm permission")
                exactAlarmPermissionLauncher.launch(exactAlarmIntent)
            } catch (e: Exception) {
                Log.e("AlarmPermissionManager", "Failed to launch exact alarm permission request, trying app settings", e)
                try {
                    // Fallback to general app settings
                    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${activity.packageName}")
                    }
                    Log.d("AlarmPermissionManager", "Launching app settings as fallback")
                    exactAlarmPermissionLauncher.launch(appSettingsIntent)
                } catch (e2: Exception) {
                    Log.e("AlarmPermissionManager", "Failed to launch app settings", e2)
                    onResult(false)
                    onPermissionResult = null
                }
            }
        } else {
            onResult(true)
        }
    }
    
    fun refreshPermissionState() {
        _hasExactAlarmPermission.value = checkExactAlarmPermission()
    }
}
