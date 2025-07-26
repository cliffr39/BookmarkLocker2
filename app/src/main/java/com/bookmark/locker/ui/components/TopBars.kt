package com.bookmark.locker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.bookmark.locker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onViewModeClick: () -> Unit,
    onMoreClick: () -> Unit,
    showSortMenu: Boolean = false,
    showViewModeMenu: Boolean = false,
    showSettingsMenu: Boolean = false,
    onSortDismiss: () -> Unit = {},
    onViewModeDismiss: () -> Unit = {},
    onSettingsDismiss: () -> Unit = {},
    onSortSelected: (com.bookmark.locker.ui.viewmodel.SortOption) -> Unit = {},
    onViewModeSelected: (com.bookmark.locker.ui.viewmodel.ViewMode) -> Unit = {},
    onSettings: () -> Unit = {},
    onAbout: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }
            
            // Sort button with dropdown
            Box {
                IconButton(onClick = onSortClick) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = stringResource(R.string.sort_by)
                    )
                }
                SortMenu(
                    expanded = showSortMenu,
                    onDismiss = onSortDismiss,
                    onSortSelected = onSortSelected
                )
            }
            
            // View mode button with dropdown
            Box {
                IconButton(onClick = onViewModeClick) {
                    Icon(
                        Icons.Default.ViewList,
                        contentDescription = stringResource(R.string.view_mode)
                    )
                }
                ViewModeMenu(
                    expanded = showViewModeMenu,
                    onDismiss = onViewModeDismiss,
                    onViewModeSelected = onViewModeSelected
                )
            }
            
            // More options button with dropdown
            Box {
                IconButton(onClick = onMoreClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                SettingsMenu(
                    expanded = showSettingsMenu,
                    onDismiss = onSettingsDismiss,
                    onSettings = onSettings,
                    onAbout = onAbout
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(stringResource(R.string.search_bookmarks))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Hide keyboard on search
                    }
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onSearchClose) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Close search"
                )
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onMoveToFolder: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.selected_count, selectedCount),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Clear selection"
                )
            }
        },
        actions = {
            IconButton(onClick = onSelectAll) {
                Icon(
                    Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.select_all)
                )
            }
            IconButton(onClick = onMoveToFolder) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = "Move to folder"
                )
            }
            IconButton(onClick = onShare) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
