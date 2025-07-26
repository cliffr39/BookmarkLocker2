package com.bookmark.locker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "bookmark_tag_cross_ref",
    primaryKeys = ["bookmarkId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Bookmark::class,
            parentColumns = ["id"],
            childColumns = ["bookmarkId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookmarkId"]),
        Index(value = ["tagId"])
    ]
)
data class BookmarkTagCrossRef(
    val bookmarkId: Long,
    val tagId: Long
)
