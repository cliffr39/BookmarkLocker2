package com.bookmark.locker.service

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import com.bookmark.locker.data.model.GitHubRelease
import com.bookmark.locker.network.GitHubApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateCheckerService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitHubApiService: GitHubApiService,
    private val sharedPreferences: SharedPreferences
) {
    
    companion object {
        private const val TAG = "UpdateCheckerService"
        private const val PREF_LAST_UPDATE_CHECK = "last_update_check"
        private const val PREF_LAST_DISMISSED_VERSION = "last_dismissed_version"
        private const val UPDATE_CHECK_INTERVAL = 3 * 24 * 60 * 60 * 1000L // 3 days in milliseconds
    }
    
    data class UpdateInfo(
        val isUpdateAvailable: Boolean,
        val latestVersion: String? = null,
        val currentVersion: String,
        val downloadUrl: String? = null,
        val releaseNotes: String? = null
    )
    
    /**
     * Get the current app version from BuildConfig
     */
    fun getCurrentVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Failed to get current version", e)
            "Unknown"
        }
    }
    
    /**
     * Check if it's time to check for updates (respecting 24-hour interval)
     */
    fun shouldCheckForUpdates(): Boolean {
        val lastCheck = sharedPreferences.getLong(PREF_LAST_UPDATE_CHECK, 0)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastCheck) >= UPDATE_CHECK_INTERVAL
    }
    
    /**
     * Check for app updates from GitHub
     */
    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val currentVersion = getCurrentVersion()
            
            // Don't check if we recently checked
            if (!shouldCheckForUpdates()) {
                return@withContext UpdateInfo(
                    isUpdateAvailable = false,
                    currentVersion = currentVersion
                )
            }
            
            Log.d(TAG, "Checking for updates. Current version: $currentVersion")
            
            val response = gitHubApiService.getLatestRelease()
            
            if (response.isSuccessful && response.body() != null) {
                val release = response.body()!!
                
                // Update last check time
                sharedPreferences.edit()
                    .putLong(PREF_LAST_UPDATE_CHECK, System.currentTimeMillis())
                    .apply()
                
                val latestVersion = release.tagName
                val isUpdateAvailable = isNewerVersion(latestVersion, currentVersion)
                
                // Check if user previously dismissed this version
                val lastDismissedVersion = sharedPreferences.getString(PREF_LAST_DISMISSED_VERSION, "")
                val shouldShowUpdate = isUpdateAvailable && latestVersion != lastDismissedVersion
                
                Log.d(TAG, "Latest version: $latestVersion, Update available: $isUpdateAvailable, Should show: $shouldShowUpdate")
                
                return@withContext UpdateInfo(
                    isUpdateAvailable = shouldShowUpdate,
                    latestVersion = latestVersion,
                    currentVersion = currentVersion,
                    downloadUrl = release.htmlUrl,
                    releaseNotes = release.body
                )
            } else {
                Log.e(TAG, "Failed to fetch latest release: ${response.errorBody()?.string()}")
                return@withContext UpdateInfo(
                    isUpdateAvailable = false,
                    currentVersion = currentVersion
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates", e)
            return@withContext UpdateInfo(
                isUpdateAvailable = false,
                currentVersion = getCurrentVersion()
            )
        }
    }
    
    /**
     * Compare two version strings to determine if the first is newer
     */
    private fun isNewerVersion(latestVersion: String, currentVersion: String): Boolean {
        try {
            // Remove common prefixes and normalize
            val latest = normalizeVersion(latestVersion)
            val current = normalizeVersion(currentVersion)
            
            Log.d(TAG, "Comparing versions: latest='$latest' vs current='$current'")
            
            val latestParts = latest.split(".")
            val currentParts = current.split(".")
            
            val maxLength = maxOf(latestParts.size, currentParts.size)
            
            for (i in 0 until maxLength) {
                val latestPart = latestParts.getOrNull(i)?.toIntOrNull() ?: 0
                val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
                
                when {
                    latestPart > currentPart -> return true
                    latestPart < currentPart -> return false
                    // Continue to next part if equal
                }
            }
            
            return false // Versions are equal
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing versions: $latestVersion vs $currentVersion", e)
            return false
        }
    }
    
    /**
     * Normalize version string for comparison
     */
    private fun normalizeVersion(version: String): String {
        return version
            .removePrefix("v")
            .removePrefix("V")
            .replace(Regex("[^0-9.]"), "") // Remove non-numeric characters except dots
            .let { if (it.isEmpty()) "0.0.0" else it }
    }
    
    /**
     * Mark a version as dismissed by the user
     */
    fun dismissVersion(version: String) {
        sharedPreferences.edit()
            .putString(PREF_LAST_DISMISSED_VERSION, version)
            .apply()
        Log.d(TAG, "Version $version dismissed by user")
    }
    
    /**
     * Force a fresh check (ignores the 24-hour interval)
     */
    fun forceUpdateCheck() {
        sharedPreferences.edit()
            .remove(PREF_LAST_UPDATE_CHECK)
            .apply()
    }
}
