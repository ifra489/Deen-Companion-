package com.deencompanion.app.data.repository


import com.deencompanion.app.data.local.dao.GoalDao
import com.deencompanion.app.data.local.dao.HabitDao
import com.deencompanion.app.data.local.dao.QazaNamazDao
import com.deencompanion.app.data.local.dao.TasbeehDao
import com.deencompanion.app.domain.model.Achievement
import com.deencompanion.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val tasbeehDao: TasbeehDao,
    private val habitDao: HabitDao,
    private val goalDao: GoalDao,
    private val qazaNamazDao: QazaNamazDao
) : AchievementRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun getAchievements(): Flow<List<Achievement>> {
        return combine(
            tasbeehDao.getAllCounts(),
            habitDao.getAllCompletionsFlow(),
            goalDao.getAllGoals(),
            qazaNamazDao.getAllPrayers()
        ) { tasbeehCounts, habitCompletions, goals, qazaPrayers ->

            val totalDhikrCount = tasbeehCounts.sumOf { it.count }
            val goalsCompleted = goals.count { it.progressCurrent >= it.progressTarget && it.progressTarget > 0 }
            val bestHabitStreak = calculateBestStreak(habitCompletions.filter { it.completed }.map { it.habitId to it.date })
            val totalQazaCompleted = qazaPrayers.sumOf { it.completedCount }

            listOf(
                Achievement(
                    id = "first_goal",
                    title = "First Steps",
                    description = "Complete your first goal",
                    currentValue = goalsCompleted.coerceAtMost(1),
                    targetValue = 1,
                    isUnlocked = goalsCompleted >= 1
                ),
                Achievement(
                    id = "goal_getter",
                    title = "Goal Getter",
                    description = "Complete 5 goals",
                    currentValue = goalsCompleted.coerceAtMost(5),
                    targetValue = 5,
                    isUnlocked = goalsCompleted >= 5
                ),
                Achievement(
                    id = "consistent_week",
                    title = "Consistent Believer",
                    description = "Maintain a 7 day habit streak",
                    currentValue = bestHabitStreak.coerceAtMost(7),
                    targetValue = 7,
                    isUnlocked = bestHabitStreak >= 7
                ),
                Achievement(
                    id = "consistent_month",
                    title = "Steadfast",
                    description = "Maintain a 30 day habit streak",
                    currentValue = bestHabitStreak.coerceAtMost(30),
                    targetValue = 30,
                    isUnlocked = bestHabitStreak >= 30
                ),
                Achievement(
                    id = "dhikr_devotee",
                    title = "Dhikr Devotee",
                    description = "Reach 1000 total dhikr count",
                    currentValue = totalDhikrCount.coerceAtMost(1000),
                    targetValue = 1000,
                    isUnlocked = totalDhikrCount >= 1000
                ),
                Achievement(
                    id = "dhikr_master",
                    title = "Dhikr Master",
                    description = "Reach 10,000 total dhikr count",
                    currentValue = totalDhikrCount.coerceAtMost(10000),
                    targetValue = 10000,
                    isUnlocked = totalDhikrCount >= 10000
                ),
                Achievement(
                    id = "making_amends",
                    title = "Making Amends",
                    description = "Complete 100 Qaza prayers",
                    currentValue = totalQazaCompleted.coerceAtMost(100),
                    targetValue = 100,
                    isUnlocked = totalQazaCompleted >= 100
                ),
                Achievement(
                    id = "qaza_warrior",
                    title = "Qaza Warrior",
                    description = "Complete 1000 Qaza prayers",
                    currentValue = totalQazaCompleted.coerceAtMost(1000),
                    targetValue = 1000,
                    isUnlocked = totalQazaCompleted >= 1000
                )
            )
        }
    }

    private fun calculateBestStreak(completions: List<Pair<String, String>>): Int {
        if (completions.isEmpty()) return 0
        val byHabit = completions.groupBy({ it.first }, { it.second })
        var best = 0
        byHabit.values.forEach { dates ->
            val dateSet = dates.toSet()
            val calendar = Calendar.getInstance()
            if (!dateSet.contains(dateFormat.format(calendar.time))) {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            var streak = 0
            while (dateSet.contains(dateFormat.format(calendar.time))) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            if (streak > best) best = streak
        }
        return best
    }
}