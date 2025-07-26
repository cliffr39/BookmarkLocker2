package com.bookmark.locker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookmark.locker.ai.AIDescriptionService
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.UndoAction
import com.bookmark.locker.data.model.UndoActionType
import com.bookmark.locker.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: BookmarkRepository,
    private val aiService: AIDescriptionService
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(BookmarkTab.ALL)
    val selectedTab: StateFlow<BookmarkTab> = _selectedTab.asStateFlow()
    
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    
    private val _selectedBookmarks = MutableStateFlow<Set<Long>>(emptySet())
    val selectedBookmarks: StateFlow<Set<Long>> = _selectedBookmarks.asStateFlow()
    
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()
    
    private val _sortOption = MutableStateFlow(SortOption.DATE_ADDED)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()
    
    private val _selectedTagFilters = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTagFilters: StateFlow<Set<Long>> = _selectedTagFilters.asStateFlow()
    
    private val _isTagFilterActive = MutableStateFlow(false)
    val isTagFilterActive: StateFlow<Boolean> = _isTagFilterActive.asStateFlow()
    
    // Undo action state for Snackbar
    private val _pendingUndoAction = MutableStateFlow<UndoAction?>(null)
    val pendingUndoAction: StateFlow<UndoAction?> = _pendingUndoAction.asStateFlow()
    
    val allBookmarks = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val favoriteBookmarks = repository.getFavoriteBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val readingListBookmarks = repository.getReadingListBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val allBookmarksWithTags = repository.getAllBookmarksWithTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    // Filtered bookmarks with tags based on selected filters
    val filteredBookmarksWithTags = combine(
        allBookmarksWithTags,
        selectedTagFilters,
        isTagFilterActive,
        selectedTab,
        isSearchActive,
        searchQuery,
        sortOption
    ) { flows ->
        val bookmarksWithTags = flows[0] as List<BookmarkWithTags>
        val tagFilters = flows[1] as Set<Long>
        val isFiltering = flows[2] as Boolean
        val tab = flows[3] as BookmarkTab
        val searching = flows[4] as Boolean
        val query = flows[5] as String
        val sort = flows[6] as SortOption
        
        var filteredBookmarks = bookmarksWithTags
        
        // Apply tab filtering
        filteredBookmarks = when (tab) {
            BookmarkTab.ALL -> filteredBookmarks.filter { !it.bookmark.isInReadingList }
            BookmarkTab.FAVORITES -> filteredBookmarks.filter { it.bookmark.isFavorite }
            BookmarkTab.READING_LIST -> filteredBookmarks.filter { it.bookmark.isInReadingList }
            BookmarkTab.FOLDERS -> filteredBookmarks // TODO: Add folder filtering
        }
        
        // Apply tag filtering
        if (isFiltering && tagFilters.isNotEmpty()) {
            filteredBookmarks = filteredBookmarks.filter { bookmarkWithTags ->
                val bookmarkTagIds = bookmarkWithTags.tags.map { it.id }.toSet()
                tagFilters.all { it in bookmarkTagIds } // AND logic: bookmark must have ALL selected tags
            }
        }
        
        // Apply search filtering
        if (searching && query.isNotBlank()) {
            val lowercaseQuery = query.lowercase()
            filteredBookmarks = filteredBookmarks.filter { bookmarkWithTags ->
                bookmarkWithTags.bookmark.title.lowercase().contains(lowercaseQuery) ||
                bookmarkWithTags.bookmark.url.lowercase().contains(lowercaseQuery) ||
                bookmarkWithTags.tags.any { it.name.lowercase().contains(lowercaseQuery) }
            }
        }
        
        // Apply sorting
        when (sort) {
            SortOption.DATE_ADDED -> filteredBookmarks.sortedByDescending { it.bookmark.dateAdded }
            SortOption.NAME -> filteredBookmarks.sortedBy { it.bookmark.title.lowercase() }
            SortOption.URL -> filteredBookmarks.sortedBy { it.bookmark.url.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val folders = repository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val allTags = repository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun getBookmarksByFolder(folderId: Long) = repository.getBookmarksByFolder(folderId)
    
    val searchResults = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchBookmarks(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val currentBookmarks = combine(
        selectedTab,
        allBookmarks,
        favoriteBookmarks,
        readingListBookmarks,
        isSearchActive,
        searchResults,
        sortOption
    ) { flows ->
        val tab = flows[0] as BookmarkTab
        val all = flows[1] as List<Bookmark>
        val favorites = flows[2] as List<Bookmark>
        val readingList = flows[3] as List<Bookmark>
        val searching = flows[4] as Boolean
        val search = flows[5] as List<Bookmark>
        val sort = flows[6] as SortOption
        
        val bookmarks = when {
            searching -> search
            tab == BookmarkTab.ALL -> all.filter { !it.isInReadingList } // Hide reading list items from ALL tab
            tab == BookmarkTab.FAVORITES -> favorites
            tab == BookmarkTab.READING_LIST -> readingList
            else -> all
        }
        
        // Apply sorting
        when (sort) {
            SortOption.DATE_ADDED -> bookmarks.sortedByDescending { it.dateAdded }
            SortOption.NAME -> bookmarks.sortedBy { it.title.lowercase() }
            SortOption.URL -> bookmarks.sortedBy { it.url.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun selectTab(tab: BookmarkTab) {
        _selectedTab.value = tab
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
            // Clear tag filters when exiting search mode
            clearTagFilters()
        }
    }
    
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }
    
    // Tag filtering methods
    fun toggleTagFilter(tagId: Long) {
        val current = _selectedTagFilters.value.toMutableSet()
        if (current.contains(tagId)) {
            current.remove(tagId)
        } else {
            current.add(tagId)
        }
        _selectedTagFilters.value = current
        _isTagFilterActive.value = current.isNotEmpty()
    }
    
    fun clearTagFilters() {
        _selectedTagFilters.value = emptySet()
        _isTagFilterActive.value = false
    }
    
    fun setTagFilters(tagIds: Set<Long>) {
        _selectedTagFilters.value = tagIds
        _isTagFilterActive.value = tagIds.isNotEmpty()
    }
    
    fun toggleBookmarkSelection(bookmarkId: Long) {
        val current = _selectedBookmarks.value.toMutableSet()
        if (current.contains(bookmarkId)) {
            current.remove(bookmarkId)
        } else {
            current.add(bookmarkId)
        }
        _selectedBookmarks.value = current
        _isSelectionMode.value = current.isNotEmpty()
    }
    
    fun selectAllBookmarks() {
        val allIds = currentBookmarks.value.map { it.id }.toSet()
        _selectedBookmarks.value = allIds
        _isSelectionMode.value = allIds.isNotEmpty()
    }
    
    fun clearSelection() {
        _selectedBookmarks.value = emptySet()
        _isSelectionMode.value = false
    }
    
    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            // Generate AI description if not provided
            val bookmarkWithDescription = if (bookmark.description.isNullOrEmpty()) {
                try {
                    val aiDescription = aiService.generateDescription(bookmark.url, bookmark.title)
                    bookmark.copy(description = aiDescription)
                } catch (e: Exception) {
                    bookmark // Use original if AI fails
                }
            } else {
                bookmark
            }
            repository.insertBookmark(bookmarkWithDescription)
        }
    }
    
    fun updateBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.updateBookmark(bookmark)
        }
    }
    
    // Soft-delete methods with undo support
    fun softDeleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.softDeleteBookmark(bookmark.id)
            
            // Create and show undo action
            val undoAction = UndoAction(
                type = UndoActionType.DELETE_BOOKMARK,
                bookmarkIds = listOf(bookmark.id),
                message = "${bookmark.title} moved to trash"
            )
            _pendingUndoAction.value = undoAction
        }
    }
    
    fun softDeleteSelectedBookmarks() {
        viewModelScope.launch {
            val selectedIds = selectedBookmarks.value.toList()
            val count = selectedIds.size
            
            selectedIds.forEach { id ->
                repository.softDeleteBookmark(id)
            }
            clearSelection()
            
            // Create and show undo action
            val undoAction = UndoAction(
                type = UndoActionType.DELETE_MULTIPLE_BOOKMARKS,
                bookmarkIds = selectedIds,
                message = "$count bookmark${if (count > 1) "s" else ""} moved to trash"
            )
            _pendingUndoAction.value = undoAction
        }
    }
    
    fun undoLastAction() {
        viewModelScope.launch {
            _pendingUndoAction.value?.let { undoAction ->
                when (undoAction.type) {
                    UndoActionType.DELETE_BOOKMARK,
                    UndoActionType.DELETE_MULTIPLE_BOOKMARKS -> {
                        undoAction.bookmarkIds.forEach { bookmarkId ->
                            repository.restoreBookmark(bookmarkId)
                        }
                    }
                    UndoActionType.EMPTY_TRASH -> {
                        // Can't truly undo empty trash since items are permanently deleted
                        // This would need more complex state management
                    }
                }
                _pendingUndoAction.value = null
            }
        }
    }
    
    fun clearUndoAction() {
        _pendingUndoAction.value = null
    }
    
    // Keep old methods for backward compatibility (but mark as deprecated)
    @Deprecated("Use softDeleteBookmark instead", ReplaceWith("softDeleteBookmark(bookmark)"))
    fun deleteBookmark(bookmark: Bookmark) {
        softDeleteBookmark(bookmark)
    }
    
    @Deprecated("Use softDeleteSelectedBookmarks instead", ReplaceWith("softDeleteSelectedBookmarks()"))
    fun deleteSelectedBookmarks() {
        softDeleteSelectedBookmarks()
    }
    
    fun toggleFavoriteStatus(bookmarkId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(bookmarkId, isFavorite)
        }
    }
    
    fun toggleReadingListStatus(bookmarkId: Long, isInReadingList: Boolean) {
        viewModelScope.launch {
            repository.updateReadingListStatus(bookmarkId, isInReadingList)
        }
    }
    
    fun incrementVisitCount(bookmarkId: Long) {
        viewModelScope.launch {
            repository.incrementVisitCount(bookmarkId)
        }
    }
    
    fun moveBookmarksToFolder(bookmarkIds: List<Long>, folderId: Long?) {
        viewModelScope.launch {
            bookmarkIds.forEach { bookmarkId ->
                val bookmark = repository.getBookmarkById(bookmarkId)
                bookmark?.let {
                    val updatedBookmark = it.copy(folderId = folderId)
                    repository.updateBookmark(updatedBookmark)
                }
            }
        }
    }
    
    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            repository.insertFolder(folder)
        }
    }
    
    fun updateFolder(folder: Folder) {
        viewModelScope.launch {
            repository.updateFolder(folder)
        }
    }
    
    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
        }
    }
    
    // Tag operations
    fun addTag(tag: Tag) {
        viewModelScope.launch {
            repository.insertTag(tag)
        }
    }
    
    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag)
        }
    }
    
    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }
    
    fun createTag(name: String): Tag {
        return Tag(name = name.trim())
    }
    
    fun addBookmarkWithTags(bookmark: Bookmark, tagIds: List<Long>) {
        viewModelScope.launch {
            // Generate AI description if not provided
            val bookmarkWithDescription = if (bookmark.description.isNullOrEmpty()) {
                try {
                    val aiDescription = aiService.generateDescription(bookmark.url, bookmark.title)
                    bookmark.copy(description = aiDescription)
                } catch (e: Exception) {
                    bookmark // Use original if AI fails
                }
            } else {
                bookmark
            }
            
            val bookmarkId = repository.insertBookmark(bookmarkWithDescription)
            if (tagIds.isNotEmpty()) {
                repository.setBookmarkTags(bookmarkId, tagIds)
            }
        }
    }
    
    fun updateBookmarkTags(bookmarkId: Long, tagIds: List<Long>) {
        viewModelScope.launch {
            repository.setBookmarkTags(bookmarkId, tagIds)
        }
    }
    
    suspend fun getBookmarkTags(bookmarkId: Long): List<Tag> {
        return repository.getBookmarkWithTags(bookmarkId)?.tags ?: emptyList()
    }
    
    suspend fun generateTagSuggestions(url: String, title: String = ""): List<String> {
        return try {
            val existingTagNames = allTags.value.map { it.name }
            aiService.generateTagSuggestions(url, title, existingTagNames)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun exportBookmarks(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val bookmarks = allBookmarks.value
                val folders = folders.value
                
                // Create a simple CSV format for bookmarks
                val csvContent = buildString {
                    appendLine("Title,URL,Is Favorite,Is Reading List,Date Added,Folder Name,Tags")
                    bookmarks.forEach { bookmark ->
                        val folderName = folders.find { it.id == bookmark.folderId }?.name ?: ""
                        val tags = getBookmarkTags(bookmark.id).joinToString(separator = ";") { it.name }
                        appendLine(
                            "\"${bookmark.title.replace("\"", "\"\"")}\"," +
                            "\"${bookmark.url}\"," +
                            "${bookmark.isFavorite}," +
                            "${bookmark.isInReadingList}," +
                            "${bookmark.dateAdded}," +
                            "\"${folderName.replace("\"", "\"\"")}\"," +
                            "\"${tags.replace("\"", "\"\"")}\""
                        )
                    }
                }
                onSuccess(csvContent)
            } catch (e: Exception) {
                onError("Failed to export bookmarks: ${e.message}")
            }
        }
    }
    
    fun importBookmarks(csvContent: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val lines = csvContent.lines().drop(1) // Skip header
                var importedCount = 0
                val existingBookmarks = allBookmarks.value
                val existingUrls = existingBookmarks.map { it.url.lowercase().trim() }.toSet()
                val existingFolders = folders.value.toMutableList()
                val existingTags = allTags.value.toMutableList()
                
                // Cache for created folders and tags during import to avoid duplicates
                val folderCache = mutableMapOf<String, Long>() // folderName -> folderId
                val tagCache = mutableMapOf<String, Long>() // tagName -> tagId
                
                // Initialize cache with existing folders and tags
                existingFolders.forEach { folder ->
                    folderCache[folder.name] = folder.id
                }
                existingTags.forEach { tag ->
                    tagCache[tag.name] = tag.id
                }
                
                lines.forEach { line ->
                    if (line.isNotBlank()) {
                        val parts = parseCsvLine(line)
                        if (parts.size >= 4) {
                            val url = parts[1].trim()
                            // Check if bookmark with this URL already exists
                            if (!existingUrls.contains(url.lowercase())) {
                                // Find or create folder
                                val folderName = parts.getOrNull(5)?.trim() ?: ""
                                val folderId = if (folderName.isNotEmpty()) {
                                    folderCache[folderName] ?: run {
                                        // Create new folder and cache it
                                        val newFolder = Folder(name = folderName)
                                        val newFolderId = repository.insertFolder(newFolder)
                                        folderCache[folderName] = newFolderId
                                        newFolderId
                                    }
                                } else null
                                
                                val bookmark = Bookmark(
                                    title = parts[0].trim(),
                                    url = url,
                                    isFavorite = parts[2].toBooleanStrictOrNull() ?: false,
                                    isInReadingList = parts[3].toBooleanStrictOrNull() ?: false,
                                    dateAdded = parts.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis(),
                                    folderId = folderId
                                )
                                val bookmarkId = repository.insertBookmark(bookmark)
                                
                                // Handle tags
                                val tagsString = parts.getOrNull(6)?.trim() ?: ""
                                if (tagsString.isNotEmpty()) {
                                    val tagNames = tagsString.split(";").map { it.trim() }.filter { it.isNotEmpty() }
                                    val tagIds = mutableListOf<Long>()
                                    
                                    tagNames.forEach { tagName ->
                                        val tagId = tagCache[tagName] ?: run {
                                            // Create new tag and cache it
                                            val newTag = Tag(name = tagName)
                                            val newTagId = repository.insertTag(newTag)
                                            tagCache[tagName] = newTagId
                                            newTagId
                                        }
                                        tagIds.add(tagId)
                                    }
                                    
                                    if (tagIds.isNotEmpty()) {
                                        repository.setBookmarkTags(bookmarkId, tagIds)
                                    }
                                }
                                
                                importedCount++
                            }
                        }
                    }
                }
                onSuccess(importedCount)
            } catch (e: Exception) {
                onError("Failed to import bookmarks: ${e.message}")
            }
        }
    }
    
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        
        while (i < line.length) {
            val char = line[i]
            when {
                char == '"' && !inQuotes -> inQuotes = true
                char == '"' && inQuotes -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++ // Skip next quote
                    } else {
                        inQuotes = false
                    }
                }
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
            i++
        }
        result.add(current.toString())
        return result
    }
}

enum class BookmarkTab {
    ALL, FAVORITES, READING_LIST, FOLDERS
}

enum class SortOption {
    DATE_ADDED, NAME, URL
}

enum class ViewMode {
    LIST, GRID, COMPACT
}
