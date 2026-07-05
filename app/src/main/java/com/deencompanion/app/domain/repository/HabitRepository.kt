package com.deencompanion.app.domain.repository



import com.deencompanion.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    suspend fun addCustomHabit(name: String)
    suspend fun deleteHabit(habitId: String)
    suspend fun toggleTodayCompletion(habitId: String)
}