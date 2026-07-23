package com.deencompanion.app.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deencompanion.app.data.local.entity.CachedSurahEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for caching Surah data to support offline reading of the Holy Quran.
 */
@Dao
interface QuranCacheDao {
    @Query("SELECT * FROM cached_surahs WHERE surahNumber = :surahNumber AND dataType = :dataType")
    suspend fun getCachedData(surahNumber: Int, dataType: String): CachedSurahEntity?

    @Query("SELECT DISTINCT surahNumber FROM cached_surahs")
    fun getDownloadedSurahNumbers(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: CachedSurahEntity)
}