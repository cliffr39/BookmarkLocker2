package com.bookmark.locker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookmark.locker.service.UpdateCheckerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateCheckerViewModel @Inject constructor(
    private val updateCheckerService: UpdateCheckerService
) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    init {
        // Check for updates silently when ViewModel is created
        checkForUpdates(isManualCheck = false)
    }

    fun checkForUpdates(isManualCheck: Boolean = false) {
        viewModelScope.launch {
            if (isManualCheck) {
                _updateState.value = UpdateState.Checking
            }
            try {
                val updateInfo = updateCheckerService.checkForUpdates()
                _updateState.value = if (updateInfo.isUpdateAvailable) {
                    UpdateState.UpdateAvailable(updateInfo)
                } else if (isManualCheck) {
                    UpdateState.NoUpdateFound
                } else {
                    UpdateState.NoUpdate
                }
            } catch (e: Exception) {
                _updateState.value = if (isManualCheck) {
                    UpdateState.Error(e.message ?: "Unknown error")
                } else {
                    UpdateState.NoUpdate // Silent fail for automatic checks
                }
            }
        }
    }

    fun forceCheckForUpdates() {
        updateCheckerService.forceUpdateCheck()
        checkForUpdates(isManualCheck = true)
    }

    fun dismissUpdate() {
        val currentState = _updateState.value
        if (currentState is UpdateState.UpdateAvailable) {
            updateCheckerService.dismissVersion(currentState.updateInfo.latestVersion ?: "")
            _updateState.value = UpdateState.Dismissed
        }
    }

    fun hideUpdateDialog() {
        _updateState.value = UpdateState.Hidden
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Checking : UpdateState()
        object NoUpdate : UpdateState()
        object NoUpdateFound : UpdateState()
        data class UpdateAvailable(val updateInfo: UpdateCheckerService.UpdateInfo) : UpdateState()
        data class Error(val message: String) : UpdateState()
        object Dismissed : UpdateState()
        object Hidden : UpdateState()
    }
}
