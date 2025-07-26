package com.bookmark.locker.data.dao

import androidx.room.*
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.data.model.BookmarkTagCrossRef
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.TagWithBookmarks
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    
    // Tag operations
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): Tag?
    
    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(tags: List<Tag>): List<Long>
    
    @Update
    suspend fun updateTag(tag: Tag)
    
    @Delete
    suspend fun deleteTag(tag: Tag)
    
    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: Long)
    
    // Cross-reference operations
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmarkTagCrossRef(crossRef: BookmarkTagCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmarkTagCrossRefs(crossRefs: List<BookmarkTagCrossRef>)
    
    @Delete
    suspend fun deleteBookmarkTagCrossRef(crossRef: BookmarkTagCrossRef)
    
    @Query("DELETE FROM bookmark_tag_cross_ref WHERE bookmarkId = :bookmarkId")
    suspend fun deleteAllTagsForBookmark(bookmarkId: Long)
    
    @Query("DELETE FROM bookmark_tag_cross_ref WHERE tagId = :tagId")
    suspend fun deleteAllBookmarksForTag(tagId: Long)
    
    @Query("DELETE FROM bookmark_tag_cross_ref WHERE bookmarkId = :bookmarkId AND tagId = :tagId")
    suspend fun removeTagFromBookmark(bookmarkId: Long, tagId: Long)
    
    // Relation queries
    @Transaction
    @Query("SELECT * FROM bookmarks WHERE id = :bookmarkId")
    suspend fun getBookmarkWithTags(bookmarkId: Long): BookmarkWithTags?
    
    @Transaction
    @Query("SELECT * FROM bookmarks WHERE isDeleted = 0 ORDER BY dateModified DESC")
    fun getAllBookmarksWithTags(): Flow<List<BookmarkWithTags>>
    
    @Transaction
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagWithBookmarks(tagId: Long): TagWithBookmarks?
    
    @Transaction
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTagsWithBookmarks(): Flow<List<TagWithBookmarks>>
    
    // Filtered queries
    @Transaction
    @Query("""
        SELECT DISTINCT b.* FROM bookmarks b 
        INNER JOIN bookmark_tag_cross_ref btcr ON b.id = btcr.bookmarkId 
        INNER JOIN tags t ON btcr.tagId = t.id 
        WHERE t.id IN (:tagIds) AND b.isDeleted = 0
        ORDER BY b.dateModified DESC
    """)
    fun getBookmarksWithTags(tagIds: List<Long>): Flow<List<BookmarkWithTags>>
    
    @Query("""
        SELECT COUNT(*) FROM bookmark_tag_cross_ref 
        WHERE tagId = :tagId
    """)
    suspend fun getTagUsageCount(tagId: Long): Int
    
    @Query("""
        UPDATE tags SET usageCount = (
            SELECT COUNT(*) FROM bookmark_tag_cross_ref 
            WHERE tagId = tags.id
        )
    """)
    suspend fun updateAllTagUsageCounts()
    
    // Search functionality
    @Transaction
    @Query("""
        SELECT DISTINCT b.* FROM bookmarks b 
        LEFT JOIN bookmark_tag_cross_ref btcr ON b.id = btcr.bookmarkId 
        LEFT JOIN tags t ON btcr.tagId = t.id 
        WHERE (b.title LIKE '%' || :query || '%' 
        OR b.url LIKE '%' || :query || '%' 
        OR b.description LIKE '%' || :query || '%'
        OR t.name LIKE '%' || :query || '%') 
        AND b.isDeleted = 0
        ORDER BY b.dateModified DESC
    """)
    fun searchBookmarksWithTags(query: String): Flow<List<BookmarkWithTags>>
}
