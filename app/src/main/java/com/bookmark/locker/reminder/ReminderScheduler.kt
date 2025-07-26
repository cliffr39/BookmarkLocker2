package com.bookmark.locker.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.bookmark.locker.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"
    
    private fun checkExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val hasPermission = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "Exact alarm permission check: $hasPermission")
            
            // Additional test: try to actually schedule a test alarm to verify permission
            if (hasPermission) {
                try {
                    val testIntent = Intent()
                    val testPendingIntent = PendingIntent.getBroadcast(
                        context,
                        999999, // Test ID
                        testIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    // Try to schedule a test alarm 1 second in the future, then immediately cancel
                    val testTime = System.currentTimeMillis() + 1000
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, testTime, testPendingIntent)
                    alarmManager.cancel(testPendingIntent) // Cancel immediately
                    testPendingIntent.cancel()
                    
                    Log.d(TAG, "Test alarm scheduling successful - permission verified")
                    return true
                } catch (e: SecurityException) {
                    Log.w(TAG, "Test alarm scheduling failed - permission not actually granted", e)
                    return false
                } catch (e: Exception) {
                    Log.w(TAG, "Test alarm scheduling error", e)
                    return hasPermission // Fall back to the original check
                }
            }
            hasPermission
        } else {
            Log.d(TAG, "Android version < S, no exact alarm permission needed")
            true // No permission needed for older versions
        }
    }
    
    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                Toast.makeText(context, "Please allow exact alarms for reminders to work", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Please enable exact alarm permission in system settings", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    fun scheduleReminder(
        context: Context,
        bookmarkId: Long,
        title: String,
        url: String,
        triggerTime: Long
    ): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val triggerDate = dateFormat.format(Date(triggerTime))
        val currentDate = dateFormat.format(Date())
        
        Log.d(TAG, "Scheduling reminder for bookmark $bookmarkId")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Trigger time: $triggerDate")
        Log.d(TAG, "Current time: $currentDate")
        Log.d(TAG, "Time until trigger: ${(triggerTime - System.currentTimeMillis())/1000}s")
        
        // Check if we have exact alarm permission
        if (!checkExactAlarmPermission(context)) {
            Log.w(TAG, "Exact alarm permission not granted")
            Toast.makeText(context, "Exact alarm permission required for reminders", Toast.LENGTH_LONG).show()
            requestExactAlarmPermission(context)
            return false
        }
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_BOOKMARK_ID, bookmarkId)
            putExtra(NotificationHelper.EXTRA_BOOKMARK_TITLE, title)
            putExtra(NotificationHelper.EXTRA_BOOKMARK_URL, url)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            bookmarkId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Using setExactAndAllowWhileIdle")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                Log.d(TAG, "Using setExact")
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d(TAG, "Alarm scheduled successfully")
            Toast.makeText(context, "Reminder scheduled successfully", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException, falling back to setAndAllowWhileIdle", e)
            // Handle case where exact alarms permission is not granted
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Toast.makeText(context, "Reminder scheduled (may be less precise)", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder", e)
            Toast.makeText(context, "Failed to schedule reminder: ${e.message}", Toast.LENGTH_LONG).show()
            return false
        }
    }
    
    fun cancelReminder(context: Context, bookmarkId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            bookmarkId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
