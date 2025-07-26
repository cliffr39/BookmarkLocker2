package com.bookmark.locker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.bookmark.locker.data.dao.BookmarkDao
import com.bookmark.locker.data.dao.FolderDao
import com.bookmark.locker.data.dao.TagDao
import com.bookmark.locker.data.model.Bookmark
import com.bookmark.locker.data.model.Folder
import com.bookmark.locker.data.model.Tag
import com.bookmark.locker.data.model.BookmarkTagCrossRef

@Database(
    entities = [Bookmark::class, Folder::class, Tag::class, BookmarkTagCrossRef::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookmarkDatabase : RoomDatabase() {
    
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun folderDao(): FolderDao
    abstract fun tagDao(): TagDao
    
    companion object {
        @Volatile
        private var INSTANCE: BookmarkDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add reminder columns to bookmarks table
                database.execSQL(
                    "ALTER TABLE bookmarks ADD COLUMN reminderTime INTEGER"
                )
                database.execSQL(
                    "ALTER TABLE bookmarks ADD COLUMN isReminderActive INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE bookmarks ADD COLUMN lastReminderTime INTEGER"
                )
            }
        }
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create tags table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `tags` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT,
                        `dateCreated` INTEGER NOT NULL,
                        `usageCount` INTEGER NOT NULL
                    )
                """)
                
                // Create unique index on tag name
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)
                """)
                
                // Create bookmark_tag_cross_ref table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `bookmark_tag_cross_ref` (
                        `bookmarkId` INTEGER NOT NULL,
                        `tagId` INTEGER NOT NULL,
                        PRIMARY KEY(`bookmarkId`, `tagId`),
                        FOREIGN KEY(`bookmarkId`) REFERENCES `bookmarks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)
                
                // Create indices for cross-ref table
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_bookmark_tag_cross_ref_bookmarkId` ON `bookmark_tag_cross_ref` (`bookmarkId`)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_bookmark_tag_cross_ref_tagId` ON `bookmark_tag_cross_ref` (`tagId`)
                """)
                
                // Migrate existing string tags to new tag system
                database.execSQL("""
                    INSERT OR IGNORE INTO tags (name, dateCreated, usageCount)
                    SELECT DISTINCT 
                        TRIM(tag_name) as name,
                        ${System.currentTimeMillis()} as dateCreated,
                        0 as usageCount
                    FROM (
                        SELECT TRIM(SUBSTR(tags, instr(',' || tags || ',', ',' || numbers.n || ',') + 1,
                            CASE WHEN instr(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1), ',') - 1 > 0
                                 THEN instr(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1), ',') - 1
                                 ELSE LENGTH(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1))
                            END)) AS tag_name
                        FROM bookmarks
                        CROSS JOIN (
                            SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
                        ) AS numbers
                        WHERE tags IS NOT NULL AND tags != ''
                        AND instr(',' || tags || ',', ',' || numbers.n || ',') > 0
                        AND TRIM(SUBSTR(tags, instr(',' || tags || ',', ',' || numbers.n || ',') + 1,
                            CASE WHEN instr(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1), ',') - 1 > 0
                                 THEN instr(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1), ',') - 1
                                 ELSE LENGTH(SUBSTR(',' || tags || ',', instr(',' || tags || ',', ',' || numbers.n || ',') + 1))
                            END)) != ''
                    )
                    WHERE tag_name IS NOT NULL AND tag_name != ''
                """)
            }
        }
        
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add soft-delete columns to bookmarks table
                database.execSQL(
                    "ALTER TABLE bookmarks ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE bookmarks ADD COLUMN deletedAt INTEGER"
                )
            }
        }
        
        fun getDatabase(context: Context): BookmarkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookmarkDatabase::class.java,
                    "bookmarks_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
