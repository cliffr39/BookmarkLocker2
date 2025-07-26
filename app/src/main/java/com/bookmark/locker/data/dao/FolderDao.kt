package com.bookmark.locker.data.dao

import androidx.room.*
import com.bookmark.locker.data.model.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    
    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<Folder>>
    
    @Query("SELECT * FROM folders WHERE parentId IS NULL ORDER BY name ASC")
    fun getRootFolders(): Flow<List<Folder>>
    
    @Query("SELECT * FROM folders WHERE parentId = :parentId ORDER BY name ASC")
    fun getSubFolders(parentId: Long): Flow<List<Folder>>
    
    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Long): Folder?
    
    @Query("SELECT * FROM folders WHERE name = :name")
    suspend fun getFolderByName(name: String): Folder?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<Folder>)
    
    @Update
    suspend fun updateFolder(folder: Folder)
    
    @Delete
    suspend fun deleteFolder(folder: Folder)
    
    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: Long)
    
    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}
