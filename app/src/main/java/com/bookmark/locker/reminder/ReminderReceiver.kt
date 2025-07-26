package com.bookmark.locker.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bookmark.locker.data.database.BookmarkDatabase
import com.bookmark.locker.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var database: BookmarkDatabase
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    override fun onReceive(context: Context, intent: Intent) {
        val bookmarkId = intent.getLongExtra(NotificationHelper.EXTRA_BOOKMARK_ID, -1)
        if (bookmarkId == -1L) return
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bookmark = database.bookmarkDao().getBookmarkById(bookmarkId)
                if (bookmark != null && bookmark.isReminderActive) {
                    // Show the notification
                    notificationHelper.showReminderNotification(bookmark)
                    
                    // Update last reminder time
                    database.bookmarkDao().updateLastReminderTime(bookmarkId, System.currentTimeMillis())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
