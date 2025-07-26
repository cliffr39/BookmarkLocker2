package com.bookmark.locker.data.repository

import com.bookmark.locker.data.dao.BookmarkDao
import com.bookmark.locker.data.dao.FolderDao
import com.bookmark.locker.data.dao.TagDao
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.BookmarkTagCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val folderDao: FolderDao,
    private val tagDao: TagDao
) : BookmarkRepository {
    
    // Bookmark operations
    override fun getAllBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    
    override fun getFavoriteBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getFavoriteBookmarks()
    
    override fun getReadingListBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getReadingListBookmarks()
    
    override fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>> = 
        bookmarkDao.getBookmarksByFolder(folderId)
    
    override fun getBookmarksWithoutFolder(): Flow<List<Bookmark>> = 
        bookmarkDao.getBookmarksWithoutFolder()
    
    override fun searchBookmarks(query: String): Flow<List<Bookmark>> = 
        bookmarkDao.searchBookmarks(query)
    
    override suspend fun getBookmarkById(id: Long): Bookmark? = bookmarkDao.getBookmarkById(id)
    
    override suspend fun getBookmarkByUrl(url: String): Bookmark? = bookmarkDao.getBookmarkByUrl(url)
    
    override suspend fun insertBookmark(bookmark: Bookmark): Long = bookmarkDao.insertBookmark(bookmark)
    
    override suspend fun insertBookmarks(bookmarks: List<Bookmark>) = 
        bookmarkDao.insertBookmarks(bookmarks)
    
    override suspend fun updateBookmark(bookmark: Bookmark) = bookmarkDao.updateBookmark(bookmark)
    
    override suspend fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)
    
    override suspend fun deleteBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)
    
    override suspend fun deleteBookmarksByFolder(folderId: Long) = 
        bookmarkDao.deleteBookmarksByFolder(folderId)
    
    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) = 
        bookmarkDao.updateFavoriteStatus(id, isFavorite)
    
    override suspend fun updateReadingListStatus(id: Long, isInReadingList: Boolean) = 
        bookmarkDao.updateReadingListStatus(id, isInReadingList)
    
    override suspend fun incrementVisitCount(id: Long) = bookmarkDao.incrementVisitCount(id)
    
    override suspend fun getBookmarkCount(): Int = bookmarkDao.getBookmarkCount()
    
    override suspend fun getFavoriteCount(): Int = bookmarkDao.getFavoriteCount()
    
    override suspend fun getReadingListCount(): Int = bookmarkDao.getReadingListCount()
    
    // Folder operations
    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()
    
    override fun getRootFolders(): Flow<List<Folder>> = folderDao.getRootFolders()
    
    override fun getSubFolders(parentId: Long): Flow<List<Folder>> = folderDao.getSubFolders(parentId)
    
    override suspend fun getFolderById(id: Long): Folder? = folderDao.getFolderById(id)
    
    override suspend fun getFolderByName(name: String): Folder? = folderDao.getFolderByName(name)
    
    override suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder)
    
    override suspend fun insertFolders(folders: List<Folder>) = folderDao.insertFolders(folders)
    
    override suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder)
    
    override suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)
    
    override suspend fun deleteFolderById(id: Long) = folderDao.deleteFolderById(id)
    
    override suspend fun getFolderCount(): Int = folderDao.getFolderCount()
    
    // Tag operations
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    
    override suspend fun getTagById(id: Long): Tag? = tagDao.getTagById(id)
    
    override suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)
    
    override suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)
    
    override suspend fun insertTags(tags: List<Tag>): List<Long> = tagDao.insertTags(tags)
    
    override suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)
    
    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
    
    override suspend fun deleteTagById(id: Long) = tagDao.deleteTagById(id)
    
    // Bookmark-Tag operations
    override fun getAllBookmarksWithTags(): Flow<List<BookmarkWithTags>> = 
        tagDao.getAllBookmarksWithTags()
    
    override suspend fun getBookmarkWithTags(bookmarkId: Long): BookmarkWithTags? = 
        tagDao.getBookmarkWithTags(bookmarkId)
    
    override fun getBookmarksWithTags(tagIds: List<Long>): Flow<List<BookmarkWithTags>> = 
        tagDao.getBookmarksWithTags(tagIds)
    
    override suspend fun addTagToBookmark(bookmarkId: Long, tagId: Long) {
        tagDao.insertBookmarkTagCrossRef(BookmarkTagCrossRef(bookmarkId, tagId))
        // Update tag usage count
        val count = tagDao.getTagUsageCount(tagId)
        tagDao.getTagById(tagId)?.let { tag ->
            tagDao.updateTag(tag.copy(usageCount = count))
        }
    }
    
    override suspend fun removeTagFromBookmark(bookmarkId: Long, tagId: Long) {
        tagDao.removeTagFromBookmark(bookmarkId, tagId)
        // Update tag usage count
        val count = tagDao.getTagUsageCount(tagId)
        tagDao.getTagById(tagId)?.let { tag ->
            tagDao.updateTag(tag.copy(usageCount = count))
        }
    }
    
    override suspend fun setBookmarkTags(bookmarkId: Long, tagIds: List<Long>) {
        // Remove all existing tags for this bookmark
        tagDao.deleteAllTagsForBookmark(bookmarkId)
        
        // Add new tags
        if (tagIds.isNotEmpty()) {
            val crossRefs = tagIds.map { tagId ->
                BookmarkTagCrossRef(bookmarkId, tagId)
            }
            tagDao.insertBookmarkTagCrossRefs(crossRefs)
        }
        
        // Update all tag usage counts
        tagDao.updateAllTagUsageCounts()
    }
    
    override suspend fun deleteAllTagsForBookmark(bookmarkId: Long) {
        tagDao.deleteAllTagsForBookmark(bookmarkId)
        // Update all tag usage counts
        tagDao.updateAllTagUsageCounts()
    }
    
    override fun searchBookmarksWithTags(query: String): Flow<List<BookmarkWithTags>> = 
        tagDao.searchBookmarksWithTags(query)
    
    // Soft-delete operations
    override suspend fun softDeleteBookmark(id: Long) {
        bookmarkDao.softDeleteBookmark(id, System.currentTimeMillis())
    }
    
    override suspend fun restoreBookmark(id: Long) {
        bookmarkDao.restoreBookmark(id)
    }
    
    override fun getDeletedBookmarks(): Flow<List<Bookmark>> = 
        bookmarkDao.getDeletedBookmarks()
    
    override suspend fun permanentlyDeleteOldBookmarks(daysOld: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        bookmarkDao.permanentlyDeleteOldBookmarks(cutoffTime)
    }
    
    override suspend fun emptyTrash() {
        bookmarkDao.emptyTrash()
    }
    
    override suspend fun getDeletedBookmarkCount(): Int = 
        bookmarkDao.getDeletedBookmarkCount()
}
