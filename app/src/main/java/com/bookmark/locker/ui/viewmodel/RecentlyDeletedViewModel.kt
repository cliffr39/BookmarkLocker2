package com.bookmark.locker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyDeletedViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    val deletedBookmarks = bookmarkRepository.getDeletedBookmarks()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedBookmarks = MutableStateFlow<Set<Long>>(emptySet())
    val selectedBookmarks: StateFlow<Set<Long>> = _selectedBookmarks.asStateFlow()

    fun toggleSelection(bookmarkId: Long) {
        _selectedBookmarks.value = if (_selectedBookmarks.value.contains(bookmarkId)) {
            _selectedBookmarks.value - bookmarkId
        } else {
            _selectedBookmarks.value + bookmarkId
        }
    }

    fun clearSelection() {
        _selectedBookmarks.value = emptySet()
    }

    fun restoreBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                bookmarkRepository.restoreBookmark(bookmarkId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun restoreSelectedBookmarks() {
        viewModelScope.launch {
            try {
                _selectedBookmarks.value.forEach { bookmarkId ->
                    bookmarkRepository.restoreBookmark(bookmarkId)
                }
                clearSelection()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun permanentlyDeleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                bookmarkRepository.deleteBookmarkById(bookmarkId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun permanentlyDeleteSelectedBookmarks() {
        viewModelScope.launch {
            try {
                _selectedBookmarks.value.forEach { bookmarkId ->
                    bookmarkRepository.deleteBookmarkById(bookmarkId)
                }
                clearSelection()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            try {
                bookmarkRepository.emptyTrash()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
