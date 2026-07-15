package com.deencompanion.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<com.deencompanion.app.data.local.entity.BookmarkEntity>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE id = :id")
    fun isBookmarked(id: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: com.deencompanion.app.data.local.entity.BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: String)
}