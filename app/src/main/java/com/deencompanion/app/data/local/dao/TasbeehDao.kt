package com.deencompanion.app.data.local.dao

import androidx.room.*
import com.deencompanion.app.data.local.entity.TasbeehCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbeehDao {
    @Query("SELECT * FROM tasbeeh_counts ORDER BY isCustom ASC, lastUpdated DESC")
    fun getAllCounts(): Flow<List<TasbeehCountEntity>>

    @Query("SELECT * FROM tasbeeh_counts WHERE type = :type")
    suspend fun getCountByType(type: String): TasbeehCountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: TasbeehCountEntity)

    @Query("UPDATE tasbeeh_counts SET count = count + 1, lastUpdated = :timestamp WHERE type = :type")
    suspend fun incrementCountInternal(type: String, timestamp: Long)

    @Query("UPDATE tasbeeh_counts SET count = 0, lastUpdated = :timestamp WHERE type = :type")
    suspend fun resetCountInternal(type: String, timestamp: Long)

    @Query("UPDATE tasbeeh_counts SET targetCount = :target WHERE type = :type")
    suspend fun updateTargetCountInternal(type: String, target: Int)

    @Query("DELETE FROM tasbeeh_counts WHERE type = :type")
    suspend fun deleteByType(type: String)

    @Transaction
    suspend fun incrementCount(type: String, defaultDisplayName: String, isCustom: Boolean, timestamp: Long) {
        val current = getCountByType(type)
        if (current == null) {
            insertOrUpdate(
                TasbeehCountEntity(
                    type = type,
                    count = 1,
                    lastUpdated = timestamp,
                    isCustom = isCustom,
                    displayName = defaultDisplayName,
                    targetCount = 33
                )
            )
        } else {
            incrementCountInternal(type, timestamp)
        }
    }

    @Transaction
    suspend fun resetCount(type: String, defaultDisplayName: String, isCustom: Boolean, timestamp: Long) {
        val current = getCountByType(type)
        if (current == null) {
            insertOrUpdate(
                TasbeehCountEntity(
                    type = type,
                    count = 0,
                    lastUpdated = timestamp,
                    isCustom = isCustom,
                    displayName = defaultDisplayName,
                    targetCount = 33
                )
            )
        } else {
            resetCountInternal(type, timestamp)
        }
    }

    @Transaction
    suspend fun updateTargetCount(type: String, defaultDisplayName: String, isCustom: Boolean, target: Int, timestamp: Long) {
        val current = getCountByType(type)
        if (current == null) {
            insertOrUpdate(
                TasbeehCountEntity(
                    type = type,
                    count = 0,
                    lastUpdated = timestamp,
                    isCustom = isCustom,
                    displayName = defaultDisplayName,
                    targetCount = target
                )
            )
        } else {
            updateTargetCountInternal(type, target)
        }
    }
}