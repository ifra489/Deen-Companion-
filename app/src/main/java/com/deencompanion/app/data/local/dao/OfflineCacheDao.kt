package com.deencompanion.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deencompanion.app.data.local.entity.OfflineCacheEntity

@Dao
interface OfflineCacheDao {
    @Query("SELECT * FROM offline_cache WHERE `key` = :key")
    suspend fun getCachedData(key: String): OfflineCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: OfflineCacheEntity)
}
