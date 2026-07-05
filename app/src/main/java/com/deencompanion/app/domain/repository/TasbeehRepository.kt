package com.deencompanion.app.domain.repository

import com.deencompanion.app.domain.model.TasbeehItem
import kotlinx.coroutines.flow.Flow

interface TasbeehRepository {
    fun getAllTasbeehItems(): Flow<List<TasbeehItem>>
    suspend fun incrementCount(id: String)
    suspend fun resetCount(id: String)
    suspend fun addCustomDhikr(displayName: String, targetCount: Int = 33): String
    suspend fun deleteCustomDhikr(id: String)
    suspend fun updateTargetCount(id: String, target: Int)
}