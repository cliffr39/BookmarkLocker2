package com.bookmark.locker.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class BookmarkWithTags(
    @Embedded val bookmark: Bookmark,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(BookmarkTagCrossRef::class, parentColumn = "bookmarkId", entityColumn = "tagId")
    )
    val tags: List<Tag>
)

data class TagWithBookmarks(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(BookmarkTagCrossRef::class, parentColumn = "tagId", entityColumn = "bookmarkId")
    )
    val bookmarks: List<Bookmark>
)
