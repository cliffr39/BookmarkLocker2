package com.bookmark.locker.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "bookmark_settings"
        private const val KEY_RINGTONE_URI = "ringtone_uri"
        private const val KEY_RINGTONE_NAME = "ringtone_name"
        private const val KEY_SNOOZE_DURATION = "snooze_duration"
        
        // Default snooze duration: 1 hour
        private const val DEFAULT_SNOOZE_DURATION = 60 * 60 * 1000L
    }

    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedRingtoneUri = MutableStateFlow<Uri?>(null)
    val selectedRingtoneUri: StateFlow<Uri?> = _selectedRingtoneUri.asStateFlow()

    private val _selectedRingtoneName = MutableStateFlow("")
    val selectedRingtoneName: StateFlow<String> = _selectedRingtoneName.asStateFlow()

    private val _snoozeDuration = MutableStateFlow(DEFAULT_SNOOZE_DURATION)
    val snoozeDuration: StateFlow<Long> = _snoozeDuration.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Load ringtone URI
        val uriString = sharedPreferences.getString(KEY_RINGTONE_URI, null)
        val uri = uriString?.let { Uri.parse(it) }
        _selectedRingtoneUri.value = uri

        // Load ringtone name
        val ringtoneName = sharedPreferences.getString(KEY_RINGTONE_NAME, "") ?: ""
        _selectedRingtoneName.value = ringtoneName

        // Load snooze duration
        val snoozeDuration = sharedPreferences.getLong(KEY_SNOOZE_DURATION, DEFAULT_SNOOZE_DURATION)
        _snoozeDuration.value = snoozeDuration

        // If we have a URI but no name, try to get the name
        if (uri != null && ringtoneName.isEmpty()) {
            updateRingtoneName(uri)
        }
    }

    fun setRingtoneUri(uri: Uri?) {
        _selectedRingtoneUri.value = uri
        
        // Save to SharedPreferences
        with(sharedPreferences.edit()) {
            if (uri != null) {
                putString(KEY_RINGTONE_URI, uri.toString())
            } else {
                remove(KEY_RINGTONE_URI)
            }
            apply()
        }

        // Update the display name
        updateRingtoneName(uri)
    }

    private fun updateRingtoneName(uri: Uri?) {
        val name = when {
            uri == null -> "Silent"
            uri == RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) -> "Default notification sound"
            else -> {
                try {
                    val ringtone = RingtoneManager.getRingtone(context, uri)
                    ringtone?.getTitle(context) ?: "Custom ringtone"
                } catch (e: Exception) {
                    "Custom ringtone"
                }
            }
        }
        
        _selectedRingtoneName.value = name
        
        // Save to SharedPreferences
        with(sharedPreferences.edit()) {
            putString(KEY_RINGTONE_NAME, name)
            apply()
        }
    }

    fun setSnoozeDuration(durationMs: Long) {
        _snoozeDuration.value = durationMs
        
        // Save to SharedPreferences
        with(sharedPreferences.edit()) {
            putLong(KEY_SNOOZE_DURATION, durationMs)
            apply()
        }
    }

    fun getRingtoneUri(): Uri? {
        return _selectedRingtoneUri.value
    }

    fun getSnoozeDurationMs(): Long {
        return _snoozeDuration.value
    }
}
