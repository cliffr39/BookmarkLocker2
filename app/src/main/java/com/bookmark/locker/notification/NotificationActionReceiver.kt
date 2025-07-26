package com.bookmark.locker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bookmark.locker.data.database.BookmarkDatabase
import com.bookmark.locker.reminder.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var database: BookmarkDatabase
    
    @Inject 
    lateinit var notificationHelper: NotificationHelper
    
    override fun onReceive(context: Context, intent: Intent) {
        val bookmarkId = intent.getLongExtra(NotificationHelper.EXTRA_BOOKMARK_ID, -1)
        if (bookmarkId == -1L) return
        
        when (intent.action) {
            NotificationHelper.ACTION_MARK_AS_READ -> {
                handleMarkAsRead(context, bookmarkId)
            }
            NotificationHelper.ACTION_SNOOZE -> {
                val title = intent.getStringExtra(NotificationHelper.EXTRA_BOOKMARK_TITLE) ?: "Bookmark"
                val url = intent.getStringExtra(NotificationHelper.EXTRA_BOOKMARK_URL) ?: ""
                handleSnooze(context, bookmarkId, title, url)
            }
        }
    }
    
    private fun handleMarkAsRead(context: Context, bookmarkId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Remove from reading list and cancel reminder
                database.bookmarkDao().updateReadingListStatus(bookmarkId, false)
                database.bookmarkDao().updateReminderStatus(bookmarkId, false, null)
                
                // Cancel any scheduled reminders
                ReminderScheduler.cancelReminder(context, bookmarkId)
                
                // Cancel the notification
                notificationHelper.cancelReminderNotification(bookmarkId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun handleSnooze(context: Context, bookmarkId: Long, title: String, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get custom snooze duration from settings
                val snoozeDuration = getCustomSnoozeDuration(context)
                val snoozeTime = System.currentTimeMillis() + snoozeDuration
                
                // Update reminder time in database
                database.bookmarkDao().updateReminderStatus(bookmarkId, true, snoozeTime)
                database.bookmarkDao().updateLastReminderTime(bookmarkId, System.currentTimeMillis())
                
                // Schedule new reminder
                ReminderScheduler.scheduleReminder(context, bookmarkId, title, url, snoozeTime)
                
                // Cancel current notification
                notificationHelper.cancelReminderNotification(bookmarkId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun getCustomSnoozeDuration(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences("bookmark_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("snooze_duration", TimeUnit.HOURS.toMillis(1)) // Default 1 hour
    }
}
