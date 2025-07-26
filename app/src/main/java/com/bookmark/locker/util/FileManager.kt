package com.bookmark.locker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileManager(private val activity: FragmentActivity) {
    
    private var exportLauncher: ActivityResultLauncher<Intent>? = null
    private var importLauncher: ActivityResultLauncher<Intent>? = null
    private var onExportReady: ((String) -> Unit)? = null
    private var onImportReady: ((String) -> Unit)? = null
    
    fun initialize() {
        // Export file launcher
        exportLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == FragmentActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    onExportReady?.let { callback ->
                        // We have the callback with CSV content, now write it to file
                        exportToFile(uri, callback)
                    }
                }
            }
        }
        
        // Import file launcher
        importLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == FragmentActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    importFromFile(uri)
                }
            }
        }
    }
    
    fun exportBookmarks(csvContent: String) {
        onExportReady = { content ->
            // This will be called when we have both the file URI and the content
            // For now, we'll just store the content and write it in exportToFile
        }
        
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "bookmarks_export.csv")
        }
        
        exportLauncher?.launch(intent)
        
        // Store the content to write when file is selected
        pendingExportContent = csvContent
    }
    
    fun importBookmarks(onImportComplete: (String) -> Unit) {
        onImportReady = onImportComplete
        
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
        }
        
        importLauncher?.launch(intent)
    }
    
    private var pendingExportContent: String? = null
    
    private fun exportToFile(uri: Uri, callback: (String) -> Unit) {
        val content = pendingExportContent ?: return
        
        try {
            activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            Toast.makeText(activity, "Bookmarks exported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(activity, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun importFromFile(uri: Uri) {
        try {
            activity.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val content = reader.readText()
                    onImportReady?.invoke(content)
                    Toast.makeText(activity, "Bookmarks imported successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
