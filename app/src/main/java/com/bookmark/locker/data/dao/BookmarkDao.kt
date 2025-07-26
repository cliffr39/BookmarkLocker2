package com.bookmark.locker.data.dao

import androidx.room.*
import com.bookmark.locker.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    
    @Query("SELECT * FROM bookmarks WHERE isDeleted = 0 ORDER BY dateAdded DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE isFavorite = 1 AND isDeleted = 0 ORDER BY dateAdded DESC")
    fun getFavoriteBookmarks(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE isInReadingList = 1 AND isDeleted = 0 ORDER BY dateAdded DESC")
    fun getReadingListBookmarks(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE folderId = :folderId AND isDeleted = 0 ORDER BY dateAdded DESC")
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE folderId IS NULL AND isDeleted = 0 ORDER BY dateAdded DESC")
    fun getBookmarksWithoutFolder(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE (title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%') AND isDeleted = 0 ORDER BY dateAdded DESC")
    fun searchBookmarks(query: String): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Long): Bookmark?
    
    @Query("SELECT * FROM bookmarks WHERE url = :url")
    suspend fun getBookmarkByUrl(url: String): Bookmark?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<Bookmark>)
    
    @Update
    suspend fun updateBookmark(bookmark: Bookmark)
    
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
    
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
    
    @Query("DELETE FROM bookmarks WHERE folderId = :folderId")
    suspend fun deleteBookmarksByFolder(folderId: Long)
    
    @Query("UPDATE bookmarks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("UPDATE bookmarks SET isInReadingList = :isInReadingList WHERE id = :id")
    suspend fun updateReadingListStatus(id: Long, isInReadingList: Boolean)
    
    @Query("UPDATE bookmarks SET visitCount = visitCount + 1 WHERE id = :id")
    suspend fun incrementVisitCount(id: Long)
    
    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE isFavorite = 1")
    suspend fun getFavoriteCount(): Int
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE isInReadingList = 1")
    suspend fun getReadingListCount(): Int
    
    // Reminder-related methods
    @Query("UPDATE bookmarks SET isReminderActive = :isActive, reminderTime = :reminderTime WHERE id = :id")
    suspend fun updateReminderStatus(id: Long, isActive: Boolean, reminderTime: Long?)
    
    @Query("UPDATE bookmarks SET lastReminderTime = :lastReminderTime WHERE id = :id")
    suspend fun updateLastReminderTime(id: Long, lastReminderTime: Long?)
    
    @Query("UPDATE bookmarks SET reminderTime = :reminderTime WHERE id = :id")
    suspend fun updateReminderTime(id: Long, reminderTime: Long?)
    
    @Query("SELECT * FROM bookmarks WHERE isReminderActive = 1 AND reminderTime <= :currentTime")
    suspend fun getActiveReminders(currentTime: Long): List<Bookmark>
    
    @Query("SELECT * FROM bookmarks WHERE isReminderActive = 1")
    fun getAllActiveReminders(): Flow<List<Bookmark>>
    
    // Soft-delete related methods
    @Query("UPDATE bookmarks SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteBookmark(id: Long, deletedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE bookmarks SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreBookmark(id: Long)
    
    @Query("SELECT * FROM bookmarks WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedBookmarks(): Flow<List<Bookmark>>
    
    @Query("DELETE FROM bookmarks WHERE isDeleted = 1 AND deletedAt < :cutoffTime")
    suspend fun permanentlyDeleteOldBookmarks(cutoffTime: Long)
    
    @Query("DELETE FROM bookmarks WHERE isDeleted = 1")
    suspend fun emptyTrash()
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE isDeleted = 1")
    suspend fun getDeletedBookmarkCount(): Int
}
