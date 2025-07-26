package com.bookmark.locker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.bookmark.locker.permission.PermissionManager
import com.bookmark.locker.ui.theme.PixelBookmarksTheme
import com.bookmark.locker.ui.screen.BookmarkScreen
import com.bookmark.locker.worker.WorkManagerInitializer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private lateinit var permissionManager: PermissionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize permission manager before setContent
        permissionManager = PermissionManager(this)
        android.util.Log.d("MainActivity", "PermissionManager initialized")
        
        // Initialize WorkManager for automatic cleanup
        WorkManagerInitializer.initialize(this)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            PixelBookmarksTheme {
                SetupSystemUI()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookmarkScreen(
                        sharedTitle = getSharedTitle(),
                        sharedUrl = getSharedUrl(),
                        permissionManager = permissionManager
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
    
    private fun getSharedTitle(): String? {
        return when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_SUBJECT)
                    ?: intent.getStringExtra(Intent.EXTRA_TITLE)
            }
            else -> null
        }
    }
    
    private fun getSharedUrl(): String? {
        return when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                    // Extract URL from shared text
                    val urlPattern = "https?://[^\\s]+".toRegex()
                    urlPattern.find(text)?.value ?: text
                }
            }
            Intent.ACTION_VIEW -> intent.dataString
            else -> null
        }
    }
}

@Composable
private fun SetupSystemUI() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !MaterialTheme.colorScheme.surface.equals(MaterialTheme.colorScheme.onSurface)
    
    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = useDarkIcons
    )
}
