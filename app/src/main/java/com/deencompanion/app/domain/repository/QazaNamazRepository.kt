package com.deencompanion.app.domain.repository

import com.deencompanion.app.domain.model.QazaPrayer
import com.deencompanion.app.domain.model.QazaSettings
import kotlinx.coroutines.flow.Flow

interface QazaNamazRepository {
    fun getSettings(): Flow<QazaSettings?>
    fun getPrayers(): Flow<List<QazaPrayer>>
    suspend fun calculateAndSave(currentAge: Int, obligationAge: Int, ageStartedPraying: Int)
    suspend fun markCompleted(prayerType: String, amount: Int)
    suspend fun resetCalculation()
}