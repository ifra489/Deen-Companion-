package com.deencompanion.app.data.repository

import com.deencompanion.app.data.local.dao.QazaNamazDao
import com.deencompanion.app.data.local.entity.QazaPrayerEntity
import com.deencompanion.app.data.local.entity.QazaSettingsEntity
import com.deencompanion.app.domain.model.QazaPrayer
import com.deencompanion.app.domain.model.QazaSettings
import com.deencompanion.app.domain.repository.QazaNamazRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QazaNamazRepositoryImpl @Inject constructor(
    private val dao: QazaNamazDao
) : QazaNamazRepository {

    private val prayerTypes = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

    override fun getSettings(): Flow<QazaSettings?> {
        return dao.getSettings().map { entity ->
            entity?.let { QazaSettings(it.currentAge, it.obligationAge, it.ageStartedPraying) }
        }
    }

    override fun getPrayers(): Flow<List<QazaPrayer>> {
        return dao.getAllPrayers().map { list ->
            list.map { QazaPrayer(it.prayerType, it.totalMissed, it.completedCount) }
        }
    }

    override suspend fun calculateAndSave(currentAge: Int, obligationAge: Int, ageStartedPraying: Int) {
        // Missed years = gap between when prayer became obligatory and when regular praying started
        val missedYears = (ageStartedPraying - obligationAge).coerceAtLeast(0)
        val missedDays = missedYears * 354 // approx Islamic lunar year

        dao.saveSettings(
            QazaSettingsEntity(
                currentAge = currentAge,
                obligationAge = obligationAge,
                ageStartedPraying = ageStartedPraying,
                calculatedAt = System.currentTimeMillis()
            )
        )

        prayerTypes.forEach { type ->
            dao.insertOrUpdatePrayer(
                QazaPrayerEntity(prayerType = type, totalMissed = missedDays, completedCount = 0)
            )
        }
    }

    override suspend fun markCompleted(prayerType: String, amount: Int) {
        dao.incrementCompleted(prayerType, amount)
    }

    override suspend fun resetCalculation() {
        dao.clearAllPrayers()
        dao.clearSettings()
    }
}