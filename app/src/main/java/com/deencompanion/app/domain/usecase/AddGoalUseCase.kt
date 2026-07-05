package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.repository.GoalRepository
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(title: String, target: Int) = repository.addGoal(title, target)
}