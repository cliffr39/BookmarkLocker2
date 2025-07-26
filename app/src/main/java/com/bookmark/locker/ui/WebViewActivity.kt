package com.bookmark.locker.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bookmark.locker.R
import java.io.IOException

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var toolbar: Toolbar
    private var isReadingMode = false
    private var forceReadingModeTheme: String? = null // "dark" or "light" to override system theme
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_enhanced)

        initViews()
        setupWebView()
        
        url = intent.getStringExtra("url") ?: "https://example.com"
        webView.loadUrl(url)
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)
        toolbar = findViewById(R.id.toolbar)
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Web Preview"
    }

    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Invalidate menu to show reading mode option
                invalidateOptionsMenu()
                // Auto-enter reading mode after page loads
                if (!isReadingMode) {
                    enterReadingMode()
                }
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                supportActionBar?.subtitle = if (newProgress < 100) "Loading... $newProgress%" else null
            }
        }
        
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val readingModeItem = menu?.findItem(R.id.action_reading_mode)
        readingModeItem?.isVisible = webView.progress == 100
        readingModeItem?.title = if (isReadingMode) "Exit Reading Mode" else "Reading Mode"
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_reading_mode -> {
                toggleReadingMode()
                true
            }
            R.id.action_theme_toggle -> {
                toggleReadingModeTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleReadingMode() {
        if (isReadingMode) {
            exitReadingMode()
        } else {
            enterReadingMode()
        }
    }

    private fun enterReadingMode() {
        // First load the Mozilla Readability.js library
        val readabilityLibrary = loadReadabilityLibrary()
        if (readabilityLibrary.isEmpty()) {
            Toast.makeText(this, "Failed to load Readability library", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Inject Readability.js and then parse the page
        val fullScript = readabilityLibrary + "\n" + getReadabilityParsingScript()
        
        webView.evaluateJavascript(fullScript) { result ->
            if (result != null && result != "null" && !result.contains("error")) {
                isReadingMode = true
                supportActionBar?.title = "Reading Mode"
                invalidateOptionsMenu()
                Toast.makeText(this, "Reading mode activated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to parse content for reading mode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exitReadingMode() {
        webView.reload()
        isReadingMode = false
        forceReadingModeTheme = null  // Reset theme override
        supportActionBar?.title = "Web Preview"
        invalidateOptionsMenu()
        Toast.makeText(this, "Reading mode deactivated", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleReadingModeTheme() {
        if (!isReadingMode) {
            Toast.makeText(this, "Theme toggle only works in reading mode", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Toggle between light, dark, and system default
        forceReadingModeTheme = when (forceReadingModeTheme) {
            null -> "light"  // Force light mode
            "light" -> "dark"  // Force dark mode  
            "dark" -> null    // Back to system default
            else -> null
        }
        
        val themeName = when (forceReadingModeTheme) {
            "light" -> "Light Theme"
            "dark" -> "Dark Theme"
            else -> "System Theme"
        }
        
        // Re-enter reading mode with new theme
        enterReadingMode()
        Toast.makeText(this, "Switched to $themeName", Toast.LENGTH_SHORT).show()
    }

    /**
     * Load the Mozilla Readability.js library from assets
     */
    private fun loadReadabilityLibrary(): String {
        return try {
            assets.open("readability.js").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
    
    /**
     * Generate the JavaScript code that uses Mozilla Readability.js to parse and style the page
     */
    private fun getReadabilityParsingScript(): String {
        // Determine theme based on forced override or system setting
        val isDarkMode = when (forceReadingModeTheme) {
            "dark" -> true
            "light" -> false
            else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
        
        return """
            (function() {
                try {
                    // Check if Readability is available
                    if (typeof Readability === 'undefined') {
                        return 'error: Readability library not loaded';
                    }
                    
                    // Create a copy of the document for Readability parsing
                    var documentClone = document.cloneNode(true);
                    
                    // Initialize Readability with the document
                    var reader = new Readability(documentClone, {
                        debug: false,
                        maxElemsToParse: 0,
                        nbTopCandidates: 5,
                        charThreshold: 500,
                        classesToPreserve: [],
                        keepClasses: false
                    });
                    
                    // Parse the document
                    var article = reader.parse();
                    
                    if (!article || !article.content) {
                        return 'error: Unable to extract readable content';
                    }
                    
                    // Apply reading mode styles
                    var backgroundColor = '${if (isDarkMode) "#1a1a1a" else "#ffffff"}';
                    var textColor = '${if (isDarkMode) "#e0e0e0" else "#333333"}';
                    var linkColor = '${if (isDarkMode) "#81C784" else "#1976D2"}';
                    
                    // Clear the document and set up base styles
                    document.head.innerHTML = `
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>` + article.title + `</title>
                        <style>
                            body {
                                background-color: ` + backgroundColor + ` !important;
                                color: ` + textColor + ` !important;
                                font-family: 'Roboto', 'Georgia', serif !important;
                                line-height: 1.6 !important;
                                max-width: 800px !important;
                                margin: 0 auto !important;
                                padding: 20px !important;
                                font-size: 18px !important;
                            }
                            h1, h2, h3, h4, h5, h6 {
                                color: ` + textColor + ` !important;
                                margin-top: 2em !important;
                                margin-bottom: 1em !important;
                                font-weight: bold !important;
                            }
                            h1 { font-size: 1.8em !important; }
                            h2 { font-size: 1.5em !important; }
                            h3 { font-size: 1.3em !important; }
                            p {
                                margin-bottom: 1.2em !important;
                                line-height: 1.7 !important;
                            }
                            a {
                                color: ` + linkColor + ` !important;
                                text-decoration: underline !important;
                            }
                            img {
                                max-width: 100% !important;
                                height: auto !important;
                                display: block !important;
                                margin: 20px auto !important;
                                border-radius: 8px !important;
                            }
                            blockquote {
                                border-left: 4px solid ` + linkColor + ` !important;
                                padding-left: 16px !important;
                                margin-left: 0 !important;
                                font-style: italic !important;
                            }
                            code {
                                background-color: ${if (isDarkMode) "#2d2d2d" else "#f5f5f5"} !important;
                                padding: 2px 4px !important;
                                border-radius: 3px !important;
                                font-family: monospace !important;
                            }
                            pre {
                                background-color: ${if (isDarkMode) "#2d2d2d" else "#f5f5f5"} !important;
                                padding: 16px !important;
                                border-radius: 8px !important;
                                overflow-x: auto !important;
                            }
                            ul, ol {
                                padding-left: 20px !important;
                            }
                            li {
                                margin-bottom: 0.5em !important;
                            }
                        </style>
                    `;
                    
                    // Create the reading container
                    document.body.innerHTML = `
                        <article>
                            <header>
                                <h1>` + article.title + `</h1>
                                ` + (article.byline ? '<p><em>by ' + article.byline + '</em></p>' : '') + `
                            </header>
                            <main>
                                ` + article.content + `
                            </main>
                        </article>
                    `;
                    
                    return 'success';
                } catch (error) {
                    return 'error: ' + error.message;
                }
            })();
        """
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack() && !isReadingMode) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
