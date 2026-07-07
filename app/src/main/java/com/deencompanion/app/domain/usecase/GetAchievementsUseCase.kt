package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.Achievement
import com.deencompanion.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke(): Flow<List<Achievement>> = repository.getAchievements()
}