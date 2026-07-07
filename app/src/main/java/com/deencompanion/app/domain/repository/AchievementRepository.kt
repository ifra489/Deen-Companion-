package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAchievements(): Flow<List<Achievement>>
}