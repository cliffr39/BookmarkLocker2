package com.bookmark.locker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.BookmarkWithTags
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.ui.viewmodel.ViewMode
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookmarkList(
    bookmarks: List<Bookmark>,
    selectedBookmarks: Set<Long>,
    isSelectionMode: Boolean,
    viewMode: ViewMode,
    onBookmarkClick: (Bookmark) -> Unit,
    onBookmarkLongClick: (Bookmark) -> Unit,
    onFavoriteToggle: (Bookmark) -> Unit,
    onReadingListToggle: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.LIST -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        isSelected = selectedBookmarks.contains(bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmark) }
                    )
                }
            }
        }
        ViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        isSelected = selectedBookmarks.contains(bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmark) }
                    )
                }
            }
        }
        ViewMode.COMPACT -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        isSelected = selectedBookmarks.contains(bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmark) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    viewMode: ViewMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onReadingListToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    when (viewMode) {
        ViewMode.GRID -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Favicon
                    AsyncImage(
                        model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = bookmark.title,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (bookmark.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        ViewMode.COMPACT -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection checkbox
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onClick() },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    // Favicon
                    AsyncImage(
                        model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = bookmark.title,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = getDomainFromUrl(bookmark.url),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Compact action buttons
                    if (!isSelectionMode) {
                        if (bookmark.isFavorite) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        if (bookmark.isInReadingList) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Reading List",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        else -> {
            // LIST view (default)
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection checkbox
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onClick() },
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                    
                    // Favicon
                    AsyncImage(
                        model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = bookmark.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = bookmark.url,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = dateFormat.format(Date(bookmark.dateAdded)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Show AI description if available
                        if (!bookmark.description.isNullOrEmpty()) {
                            Text(
                                text = bookmark.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Action buttons
                    if (!isSelectionMode) {
                        Row {
                            IconButton(
                                onClick = onFavoriteToggle,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (bookmark.isFavorite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Toggle favorite",
                                    tint = if (bookmark.isFavorite) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            
                            IconButton(
                                onClick = onReadingListToggle,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (bookmark.isInReadingList) {
                                        Icons.Default.MenuBook
                                    } else {
                                        Icons.Default.BookmarkBorder
                                    },
                                    contentDescription = "Toggle reading list",
                                    tint = if (bookmark.isInReadingList) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Overloaded BookmarkList for BookmarkWithTags
@Composable
fun BookmarkListWithTags(
    bookmarksWithTags: List<BookmarkWithTags>,
    selectedBookmarks: Set<Long>,
    isSelectionMode: Boolean,
    viewMode: ViewMode,
    onBookmarkClick: (Bookmark) -> Unit,
    onBookmarkLongClick: (Bookmark) -> Unit,
    onFavoriteToggle: (Bookmark) -> Unit,
    onReadingListToggle: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.LIST -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarksWithTags, key = { it.bookmark.id }) { bookmarkWithTags ->
                    BookmarkItemWithTags(
                        bookmarkWithTags = bookmarkWithTags,
                        isSelected = selectedBookmarks.contains(bookmarkWithTags.bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmarkWithTags.bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmarkWithTags.bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmarkWithTags.bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmarkWithTags.bookmark) }
                    )
                }
            }
        }
        ViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarksWithTags, key = { it.bookmark.id }) { bookmarkWithTags ->
                    BookmarkItemWithTags(
                        bookmarkWithTags = bookmarkWithTags,
                        isSelected = selectedBookmarks.contains(bookmarkWithTags.bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmarkWithTags.bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmarkWithTags.bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmarkWithTags.bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmarkWithTags.bookmark) }
                    )
                }
            }
        }
        ViewMode.COMPACT -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(bookmarksWithTags, key = { it.bookmark.id }) { bookmarkWithTags ->
                    BookmarkItemWithTags(
                        bookmarkWithTags = bookmarkWithTags,
                        isSelected = selectedBookmarks.contains(bookmarkWithTags.bookmark.id),
                        isSelectionMode = isSelectionMode,
                        viewMode = viewMode,
                        onClick = { onBookmarkClick(bookmarkWithTags.bookmark) },
                        onLongClick = { onBookmarkLongClick(bookmarkWithTags.bookmark) },
                        onFavoriteToggle = { onFavoriteToggle(bookmarkWithTags.bookmark) },
                        onReadingListToggle = { onReadingListToggle(bookmarkWithTags.bookmark) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkItemWithTags(
    bookmarkWithTags: BookmarkWithTags,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    viewMode: ViewMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onReadingListToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmark = bookmarkWithTags.bookmark
    val tags = bookmarkWithTags.tags
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    when (viewMode) {
        ViewMode.GRID -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Favicon
                    AsyncImage(
                        model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = bookmark.title,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Tags for grid view (limited)
                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TagDisplayRow(
                            tags = tags.take(2), // Show max 2 tags in grid
                            maxWidth = 120.dp
                        )
                    }
                    
                    if (bookmark.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        ViewMode.COMPACT -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Selection checkbox
                        if (isSelectionMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onClick() },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        
                        // Favicon
                        AsyncImage(
                            model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = bookmark.title,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = getDomainFromUrl(bookmark.url),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Compact action buttons
                        if (!isSelectionMode) {
                            if (bookmark.isFavorite) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorite",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            if (bookmark.isInReadingList) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Reading List",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Tags for compact view
                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TagDisplayRow(
                            tags = tags.take(3), // Show max 3 tags in compact
                            maxWidth = null
                        )
                    }
                }
            }
        }
        else -> {
            // LIST view (default)
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection checkbox
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onClick() },
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                    
                    // Favicon
                    AsyncImage(
                        model = bookmark.faviconUrl ?: "https://www.google.com/s2/favicons?domain=${getDomainFromUrl(bookmark.url)}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = bookmark.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = bookmark.url,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = dateFormat.format(Date(bookmark.dateAdded)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Show AI description if available
                        if (!bookmark.description.isNullOrEmpty()) {
                            Text(
                                text = bookmark.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Tags for list view
                        if (tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TagDisplayRow(
                                tags = tags, // Show all tags in list view
                                maxWidth = null
                            )
                        }
                    }
                    
                    // Action buttons
                    if (!isSelectionMode) {
                        Row {
                            IconButton(
                                onClick = onFavoriteToggle,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (bookmark.isFavorite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Toggle favorite",
                                    tint = if (bookmark.isFavorite) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            
                            IconButton(
                                onClick = onReadingListToggle,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (bookmark.isInReadingList) {
                                        Icons.Default.MenuBook
                                    } else {
                                        Icons.Default.BookmarkBorder
                                    },
                                    contentDescription = "Toggle reading list",
                                    tint = if (bookmark.isInReadingList) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagDisplayRow(
    tags: List<Tag>,
    maxWidth: androidx.compose.ui.unit.Dp?,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return
    
    LazyRow(
        modifier = modifier.then(
            if (maxWidth != null) Modifier.widthIn(max = maxWidth) else Modifier
        ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(tags) { tag ->
            TagDisplayChip(tag = tag)
        }
    }
}

@Composable
fun TagDisplayChip(
    tag: Tag,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { /* TODO: Implement tag filtering */ },
        label = {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        border = null
    )
}

private fun getDomainFromUrl(url: String): String {
    return try {
        val uri = java.net.URI(url)
        uri.host ?: url
    } catch (e: Exception) {
        url
    }
}
