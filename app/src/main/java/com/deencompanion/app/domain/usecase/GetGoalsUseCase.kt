package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.Goal
import com.deencompanion.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(private val repository: GoalRepository) {
    operator fun invoke(): Flow<List<Goal>> = repository.getGoals()
}