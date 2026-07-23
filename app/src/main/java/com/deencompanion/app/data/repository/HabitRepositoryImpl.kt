package com.deencompanion.app.data.repository


import com.deencompanion.app.data.local.dao.HabitDao
import com.deencompanion.app.data.local.entity.HabitCompletionEntity
import com.deencompanion.app.data.local.entity.HabitEntity
import com.deencompanion.app.domain.model.Habit
import com.deencompanion.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val defaultHabits = listOf(
        "Read Quran " to "default_quran",
        "Give Charity" to "default_sadqa",
        "Pray Tahajjud" to "default_tahajjud",
        "Do Dhikr" to "default_zikr"
    )

    override fun getHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().combine(habitDao.getAllCompletionsFlow()) { habits, completions ->
            val today = dateFormat.format(Date())
            val allHabits = if (habits.isEmpty()) {
                // Seed default habits on first launch
                defaultHabits.forEach { (name, id) ->
                    habitDao.insertHabit(HabitEntity(id = id, name = name, isCustom = false, createdAt = System.currentTimeMillis()))
                }
                habitDao.getAllHabits()
                emptyList() // will re-emit via flow after insert
            } else habits

            allHabits.map { habit ->
                val habitCompletions = completions.filter { it.habitId == habit.id && it.completed }
                val streak = calculateStreak(habitCompletions.map { it.date })
                val isCompletedToday = habitCompletions.any { it.date == today }
                Habit(
                    id = habit.id,
                    name = habit.name,
                    isCustom = habit.isCustom,
                    isCompletedToday = isCompletedToday,
                    currentStreak = streak
                )
            }
        }
    }

    private fun calculateStreak(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0
        val dateSet = completedDates.toSet()
        val calendar = Calendar.getInstance()
        var streak = 0

        // Agar aaj complete nahi hui, toh kal se streak check karo
        if (!dateSet.contains(dateFormat.format(calendar.time))) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (dateSet.contains(dateFormat.format(calendar.time))) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    override suspend fun addCustomHabit(name: String) {
        val id = "custom_${UUID.randomUUID()}"
        habitDao.insertHabit(HabitEntity(id = id, name = name, isCustom = true, createdAt = System.currentTimeMillis()))
    }

    override suspend fun deleteHabit(habitId: String) {
        habitDao.deleteHabit(habitId)
    }

    override suspend fun toggleTodayCompletion(habitId: String) {
        val today = dateFormat.format(Date())
        val existing = habitDao.getCompletionForDate(habitId, today)
        val newState = !(existing?.completed ?: false)
        habitDao.setCompletion(HabitCompletionEntity(habitId = habitId, date = today, completed = newState))
    }
}