package com.bookmark.locker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String? = null, // Optional color for UI display
    val dateCreated: Long = System.currentTimeMillis(),
    val usageCount: Int = 0 // Track how many bookmarks use this tag
) : Parcelable
