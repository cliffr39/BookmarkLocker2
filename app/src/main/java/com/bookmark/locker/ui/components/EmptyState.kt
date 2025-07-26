package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bookmark.locker.R
import com.bookmark.locker.ui.viewmodel.BookmarkTab

@Composable
fun EmptyState(
    tab: BookmarkTab,
    onAddBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val (icon, title, subtitle) = when (tab) {
            BookmarkTab.ALL -> Triple(
                Icons.Default.Bookmark,
                stringResource(R.string.no_bookmarks),
                stringResource(R.string.add_your_first_bookmark)
            )
            BookmarkTab.FAVORITES -> Triple(
                Icons.Default.FavoriteBorder,
                stringResource(R.string.no_favorites),
                "Mark bookmarks as favorites to see them here"
            )
            BookmarkTab.READING_LIST -> Triple(
                Icons.Default.MenuBook,
                stringResource(R.string.no_reading_list),
                "Add bookmarks to your reading list to see them here"
            )
            BookmarkTab.FOLDERS -> Triple(
                Icons.Default.Folder,
                "No folders yet",
                "Create folders to organize your bookmarks"
            )
        }
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        if (tab == BookmarkTab.ALL) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAddBookmark,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_bookmark))
            }
        }
    }
}
