package com.bookmark.locker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookmark.locker.data.repository.BookmarkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BookmarkCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookmarkRepository: BookmarkRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Delete bookmarks that have been in trash for more than 30 days
            bookmarkRepository.permanentlyDeleteOldBookmarks(30)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "bookmark_cleanup_work"
    }
}
