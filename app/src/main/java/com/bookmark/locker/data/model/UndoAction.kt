package com.bookmark.locker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UndoAction(
    val type: UndoActionType,
    val bookmarkIds: List<Long>,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

enum class UndoActionType {
    DELETE_BOOKMARK,
    DELETE_MULTIPLE_BOOKMARKS,
    EMPTY_TRASH
}
