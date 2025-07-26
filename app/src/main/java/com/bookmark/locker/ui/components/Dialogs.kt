package com.bookmark.locker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bookmark.locker.R
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.reminder.ReminderScheduler
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddBookmarkDialog(
    folders: List<Folder>,
    tags: List<Tag>,
    onDismiss: () -> Unit,
    onSave: (Bookmark, List<Long>) -> Unit,
    onCreateTag: (String) -> Unit,
    onGenerateTagSuggestions: suspend (String, String) -> List<String> = { _, _ -> emptyList() },
    initialTitle: String = "",
    initialUrl: String = ""
) {
    var title by remember { mutableStateOf(initialTitle) }
    var url by remember { mutableStateOf(initialUrl) }
    var selectedFolder by remember { mutableStateOf<Folder?>(null) }
    var selectedTags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var suggestedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingSuggestions by remember { mutableStateOf(false) }
    
    // Generate tag suggestions when URL or title changes
    LaunchedEffect(url, title) {
        android.util.Log.d("AddBookmarkDialog", "LaunchedEffect triggered: url='$url', title='$title'")
        if (url.isNotBlank() && title.isNotBlank()) {
            android.util.Log.d("AddBookmarkDialog", "Generating suggestions for URL: $url")
            isLoadingSuggestions = true
            try {
                val suggestions = onGenerateTagSuggestions(url, title)
                android.util.Log.d("AddBookmarkDialog", "Got suggestions: $suggestions")
                suggestedTags = suggestions
            } catch (e: Exception) {
                android.util.Log.e("AddBookmarkDialog", "Error generating suggestions", e)
                suggestedTags = emptyList()
            } finally {
                isLoadingSuggestions = false
            }
        } else {
            android.util.Log.d("AddBookmarkDialog", "Clearing suggestions - URL or title blank")
            suggestedTags = emptyList()
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_bookmark),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.url)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )
                
                // Folder selection
                FolderDropdown(
                    folders = folders,
                    selectedFolder = selectedFolder,
                    onFolderSelected = { selectedFolder = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Suggested tags
                SuggestedTags(
                    suggestedTags = suggestedTags,
                    isLoading = isLoadingSuggestions,
onTagSelected = { tagName ->
    android.util.Log.d("AddBookmarkDialog", "Suggested tag selected: $tagName")
    // Create the tag if it doesn't exist
    val existingTag = tags.find { it.name == tagName }
    if (existingTag == null) {
        onCreateTag(tagName)
    }
    
    // Create a temporary tag for selection (it will be properly linked when saved)
    val tempTag = existingTag ?: Tag(id = System.currentTimeMillis(), name = tagName)
    if (!selectedTags.any { it.name == tagName }) {
        selectedTags = selectedTags + tempTag
        android.util.Log.d("AddBookmarkDialog", "Tag added to selection: ${selectedTags.map { it.name }}")
    }
},
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Tag selection
                TagSelector(
                    availableTags = tags,
                    selectedTags = selectedTags,
                    onTagSelected = { tag ->
                        selectedTags = selectedTags + tag
                    },
                    onTagRemoved = { tag ->
                        selectedTags = selectedTags.filter { it.id != tag.id }
                    },
                    onCreateTag = { tagName ->
                        onCreateTag(tagName)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank() && url.isNotBlank()) {
                                onSave(
                                    Bookmark(
                                        title = title,
                                        url = url,
                                        folderId = selectedFolder?.id
                                    ),
                                    selectedTags.map { it.id }
                                )
                            }
                        },
                        enabled = title.isNotBlank() && url.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onDismiss: () -> Unit,
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().apply {
            set(initialYear, initialMonth, initialDay)
        }.timeInMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        onDateSelected(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (hourOfDay: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTimeSelected(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                }
            ) {
                Text("OK")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
fun EditBookmarkDialog(
    bookmark: Bookmark,
    folders: List<Folder>,
    tags: List<Tag>,
    bookmarkTags: List<Tag>,
    onDismiss: () -> Unit,
    onSave: (Bookmark, List<Long>) -> Unit,
    onDelete: () -> Unit,
    onCreateTag: (String) -> Unit
) {
    var title by remember { mutableStateOf(bookmark.title) }
    var url by remember { mutableStateOf(bookmark.url) }
    var description by remember { mutableStateOf(bookmark.description ?: "") }
    var selectedFolder by remember { mutableStateOf(folders.find { it.id == bookmark.folderId }) }
    var selectedTags by remember { mutableStateOf(bookmarkTags) }
    var isFavorite by remember { mutableStateOf(bookmark.isFavorite) }
    var isInReadingList by remember { mutableStateOf(bookmark.isInReadingList) }
    var hasReminder by remember { mutableStateOf(bookmark.isReminderActive) }
    var selectedReminderTime by remember { mutableStateOf(bookmark.reminderTime ?: System.currentTimeMillis() + (60 * 60 * 1000)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_bookmark),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(stringResource(R.string.url)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                // Folder selection
                FolderDropdown(
                    folders = folders,
                    selectedFolder = selectedFolder,
                    onFolderSelected = { selectedFolder = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Tag selection
                TagSelector(
                    availableTags = tags,
                    selectedTags = selectedTags,
                    onTagSelected = { tag ->
                        selectedTags = selectedTags + tag
                    },
                    onTagRemoved = { tag ->
                        selectedTags = selectedTags.filter { it.id != tag.id }
                    },
                    onCreateTag = { tagName ->
                        onCreateTag(tagName)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Toggle switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Favorite")
                    Switch(
                        checked = isFavorite,
                        onCheckedChange = { isFavorite = it }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reading List")
                    Switch(
                        checked = isInReadingList,
                        onCheckedChange = { isInReadingList = it }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Reminder")
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                }
                
                // Reminder date/time picker (only shown when hasReminder is true)
                if (hasReminder) {
                    Column {
                        Text(
                            text = "Reminder Time:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Date picker button
                            OutlinedButton(
                                onClick = { showDatePicker = true }
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(dateFormat.format(Date(selectedReminderTime)))
                            }
                            
                            // Time picker button
                            OutlinedButton(
                                onClick = { showTimePicker = true }
                            ) {
                                Text(timeFormat.format(Date(selectedReminderTime)))
                            }
                        }
                    }
                }
                
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End
) {
                    // Delete button
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank() && url.isNotBlank()) {
                                val reminderTime = if (hasReminder) selectedReminderTime else null
                                
                                val updatedBookmark = bookmark.copy(
                                    title = title,
                                    url = url,
                                    description = description.ifEmpty { null },
                                    folderId = selectedFolder?.id,
                                    isFavorite = isFavorite,
                                    isInReadingList = isInReadingList,
                                    isReminderActive = hasReminder,
                                    reminderTime = reminderTime,
                                    dateModified = System.currentTimeMillis()
                                )
                                
                                // Schedule or cancel reminder
                                if (hasReminder && reminderTime != null) {
                                    val success = ReminderScheduler.scheduleReminder(
                                        context, 
                                        bookmark.id, 
                                        title, 
                                        url, 
                                        reminderTime
                                    )
                                    // Only save if scheduling was successful
                                    if (success) {
                                        onSave(updatedBookmark, selectedTags.map { it.id })
                                    }
                                } else if (!hasReminder) {
                                    ReminderScheduler.cancelReminder(context, bookmark.id)
                                    onSave(updatedBookmark, selectedTags.map { it.id })
                                } else {
                                    onSave(updatedBookmark, selectedTags.map { it.id })
                                }
                            }
                        },
                        enabled = title.isNotBlank() && url.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedReminderTime
        
        DatePickerDialog(
            onDateSelected = { year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.timeInMillis = selectedReminderTime
                newCalendar.set(Calendar.YEAR, year)
                newCalendar.set(Calendar.MONTH, month)
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedReminderTime = newCalendar.timeInMillis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialYear = calendar.get(Calendar.YEAR),
            initialMonth = calendar.get(Calendar.MONTH),
            initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedReminderTime
        
        TimePickerDialog(
            onTimeSelected = { hourOfDay, minute ->
                val newCalendar = Calendar.getInstance()
                newCalendar.timeInMillis = selectedReminderTime
                newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                newCalendar.set(Calendar.MINUTE, minute)
                newCalendar.set(Calendar.SECOND, 0)
                selectedReminderTime = newCalendar.timeInMillis
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
    }
}

@Composable
fun AddFolderDialog(
    onDismiss: () -> Unit,
    onSave: (Folder) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_folder),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.folder_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(Folder(name = name))
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
