package com.deencompanion.app.data.repository



import com.deencompanion.app.data.local.dao.GoalDao
import com.deencompanion.app.data.local.entity.GoalEntity
import com.deencompanion.app.domain.model.Goal
import com.deencompanion.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { list ->
            list.map { Goal(it.id, it.title, it.progressCurrent, it.progressTarget) }
        }
    }

    override suspend fun addGoal(title: String, target: Int) {
        goalDao.insertGoal(
            GoalEntity(
                id = "goal_${UUID.randomUUID()}",
                title = title,
                progressCurrent = 0,
                progressTarget = target,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun incrementProgress(goalId: String, current: Int, target: Int) {
        val newValue = (current + 1).coerceAtMost(target)
        goalDao.updateProgress(goalId, newValue)
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.deleteGoal(goalId)
    }
}