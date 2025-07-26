package com.bookmark.locker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bookmark.locker.R
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.ui.WebViewActivity

class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "bookmark_reminders_v2"
        const val CHANNEL_NAME = "Bookmark Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for bookmark reading reminders"
        
        // Notification action constants
        const val ACTION_MARK_AS_READ = "action_mark_as_read"
        const val ACTION_SNOOZE = "action_snooze"
        const val ACTION_OPEN_BOOKMARK = "action_open_bookmark"
        
        // Extra keys
        const val EXTRA_BOOKMARK_ID = "bookmark_id"
        const val EXTRA_BOOKMARK_URL = "bookmark_url"
        const val EXTRA_BOOKMARK_TITLE = "bookmark_title"
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = getCustomRingtoneUri()
            android.util.Log.d("NotificationHelper", "Creating channel with sound URI: $soundUri")
            
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
            android.util.Log.d("NotificationHelper", "Notification channel created with custom sound")
        }
    }
    
    fun showReminderNotification(bookmark: Bookmark) {
        val openIntent = Intent(context, WebViewActivity::class.java).apply {
            putExtra("url", bookmark.url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val openPendingIntent = PendingIntent.getActivity(
            context,
            bookmark.id.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val markAsReadIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_AS_READ
            putExtra(EXTRA_BOOKMARK_ID, bookmark.id)
        }
        
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            context,
            "${bookmark.id}_read".hashCode(),
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_BOOKMARK_ID, bookmark.id)
            putExtra(EXTRA_BOOKMARK_TITLE, bookmark.title)
            putExtra(EXTRA_BOOKMARK_URL, bookmark.url)
        }
        
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            "${bookmark.id}_snooze".hashCode(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val soundUri = getCustomRingtoneUri()
        android.util.Log.d("NotificationHelper", "Showing notification with sound URI: $soundUri")
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_book_24)
            .setContentTitle("Time to read: ${bookmark.title}")
            .setContentText("Your reading reminder is ready")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("You set a reminder to read \"${bookmark.title}\". Tap to open in reading mode, mark as read, or snooze for later."))
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri) // For devices below Android O
            .addAction(
                R.drawable.ic_book_24, // You might want to create a specific "mark read" icon
                "Mark as Read",
                markAsReadPendingIntent
            )
            .addAction(
                R.drawable.ic_web_24, // You might want to create a specific "snooze" icon
                getSnoozeButtonText(),
                snoozePendingIntent
            )
            .build()
        
        notificationManager.notify(bookmark.id.toInt(), notification)
    }
    
    fun cancelReminderNotification(bookmarkId: Long) {
        notificationManager.cancel(bookmarkId.toInt())
    }
    
    private fun getCustomRingtoneUri(): Uri {
        val sharedPreferences = context.getSharedPreferences("bookmark_settings", Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString("ringtone_uri", null)
        
        return if (uriString != null) {
            Uri.parse(uriString)
        } else {
            // Fallback to default custom ringtone
            Uri.parse("android.resource://${context.packageName}/${R.raw.yoohoo}")
        }
    }
    
    private fun getSnoozeButtonText(): String {
        val sharedPreferences = context.getSharedPreferences("bookmark_settings", Context.MODE_PRIVATE)
        val durationMs = sharedPreferences.getLong("snooze_duration", 60 * 60 * 1000L) // Default 1 hour
        
        return when (durationMs) {
            5 * 60 * 1000L -> "Snooze 5m"
            15 * 60 * 1000L -> "Snooze 15m"
            30 * 60 * 1000L -> "Snooze 30m"
            60 * 60 * 1000L -> "Snooze 1h"
            2 * 60 * 60 * 1000L -> "Snooze 2h"
            4 * 60 * 60 * 1000L -> "Snooze 4h"
            else -> "Snooze ${durationMs / (60 * 1000)}m"
        }
    }
}
