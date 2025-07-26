package com.bookmark.locker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val folderId: Long? = null,
    val isFavorite: Boolean = false,
    val isInReadingList: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val visitCount: Int = 0,
    val description: String? = null,
    val tags: String? = null,
    val reminderTime: Long? = null, // Timestamp for when to show reminder
    val isReminderActive: Boolean = false, // Whether reminder is currently set
    val lastReminderTime: Long? = null, // Track when last reminder was shown for snoozing
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Long? = null // Timestamp when bookmark was deleted
) : Parcelable
