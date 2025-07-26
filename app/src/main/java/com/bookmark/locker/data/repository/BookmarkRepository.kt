package com.bookmark.locker.data.repository

import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.data.model.BookmarkWithTags
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    
    // Bookmark operations
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun getFavoriteBookmarks(): Flow<List<Bookmark>>
    fun getReadingListBookmarks(): Flow<List<Bookmark>>
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>>
    fun getBookmarksWithoutFolder(): Flow<List<Bookmark>>
    fun searchBookmarks(query: String): Flow<List<Bookmark>>
    suspend fun getBookmarkById(id: Long): Bookmark?
    suspend fun getBookmarkByUrl(url: String): Bookmark?
    suspend fun insertBookmark(bookmark: Bookmark): Long
    suspend fun insertBookmarks(bookmarks: List<Bookmark>)
    suspend fun updateBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun deleteBookmarkById(id: Long)
    suspend fun deleteBookmarksByFolder(folderId: Long)
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    suspend fun updateReadingListStatus(id: Long, isInReadingList: Boolean)
    suspend fun incrementVisitCount(id: Long)
    suspend fun getBookmarkCount(): Int
    suspend fun getFavoriteCount(): Int
    suspend fun getReadingListCount(): Int
    
    // Folder operations
    fun getAllFolders(): Flow<List<Folder>>
    fun getRootFolders(): Flow<List<Folder>>
    fun getSubFolders(parentId: Long): Flow<List<Folder>>
    suspend fun getFolderById(id: Long): Folder?
    suspend fun getFolderByName(name: String): Folder?
    suspend fun insertFolder(folder: Folder): Long
    suspend fun insertFolders(folders: List<Folder>)
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun deleteFolderById(id: Long)
    suspend fun getFolderCount(): Int
    
    // Tag operations
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(id: Long): Tag?
    suspend fun getTagByName(name: String): Tag?
    suspend fun insertTag(tag: Tag): Long
    suspend fun insertTags(tags: List<Tag>): List<Long>
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)
    suspend fun deleteTagById(id: Long)
    
    // Bookmark-Tag operations
    fun getAllBookmarksWithTags(): Flow<List<BookmarkWithTags>>
    suspend fun getBookmarkWithTags(bookmarkId: Long): BookmarkWithTags?
    fun getBookmarksWithTags(tagIds: List<Long>): Flow<List<BookmarkWithTags>>
    suspend fun addTagToBookmark(bookmarkId: Long, tagId: Long)
    suspend fun removeTagFromBookmark(bookmarkId: Long, tagId: Long)
    suspend fun setBookmarkTags(bookmarkId: Long, tagIds: List<Long>)
    suspend fun deleteAllTagsForBookmark(bookmarkId: Long)
    fun searchBookmarksWithTags(query: String): Flow<List<BookmarkWithTags>>
    
    // Soft-delete operations
    suspend fun softDeleteBookmark(id: Long)
    suspend fun restoreBookmark(id: Long)
    fun getDeletedBookmarks(): Flow<List<Bookmark>>
    suspend fun permanentlyDeleteOldBookmarks(daysOld: Int = 30)
    suspend fun emptyTrash()
    suspend fun getDeletedBookmarkCount(): Int
}
