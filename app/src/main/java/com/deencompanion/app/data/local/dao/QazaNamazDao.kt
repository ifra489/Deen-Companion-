package com.deencompanion.app.data.local.dao


import androidx.room.*
import com.deencompanion.app.data.local.entity.QazaPrayerEntity
import com.deencompanion.app.data.local.entity.QazaSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QazaNamazDao {
    @Query("SELECT * FROM qaza_settings WHERE id = 1")
    fun getSettings(): Flow<QazaSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: QazaSettingsEntity)

    @Query("SELECT * FROM qaza_prayers")
    fun getAllPrayers(): Flow<List<QazaPrayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayer(entity: QazaPrayerEntity)

    @Query("UPDATE qaza_prayers SET completedCount = completedCount + :amount WHERE prayerType = :type")
    suspend fun incrementCompleted(type: String, amount: Int)

    @Query("DELETE FROM qaza_prayers")
    suspend fun clearAllPrayers()

    @Query("DELETE FROM qaza_settings")
    suspend fun clearSettings()
}