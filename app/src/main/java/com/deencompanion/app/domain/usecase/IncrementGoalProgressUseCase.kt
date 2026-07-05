package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.repository.GoalRepository
import javax.inject.Inject

class IncrementGoalProgressUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: String, current: Int, target: Int) = repository.incrementProgress(goalId, current, target)
}