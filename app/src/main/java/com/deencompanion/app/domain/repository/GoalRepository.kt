package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    suspend fun addGoal(title: String, target: Int)
    suspend fun incrementProgress(goalId: String, current: Int, target: Int)
    suspend fun deleteGoal(goalId: String)
}