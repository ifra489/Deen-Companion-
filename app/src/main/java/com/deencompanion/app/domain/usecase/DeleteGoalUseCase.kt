package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: String) = repository.deleteGoal(goalId)
}