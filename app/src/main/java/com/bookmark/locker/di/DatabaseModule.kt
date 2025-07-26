package com.bookmark.locker.di

import android.content.Context
import androidx.room.Room
import com.bookmark.locker.data.dao.BookmarkDao
import com.bookmark.locker.data.dao.FolderDao
import com.bookmark.locker.data.dao.TagDao
import com.bookmark.locker.data.database.BookmarkDatabase
import com.bookmark.locker.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideBookmarkDatabase(@ApplicationContext context: Context): BookmarkDatabase {
        return BookmarkDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideBookmarkDao(database: BookmarkDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
    
    @Provides
    fun provideFolderDao(database: BookmarkDatabase): FolderDao {
        return database.folderDao()
    }
    
    @Provides
    fun provideTagDao(database: BookmarkDatabase): TagDao {
        return database.tagDao()
    }
    
    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
}
