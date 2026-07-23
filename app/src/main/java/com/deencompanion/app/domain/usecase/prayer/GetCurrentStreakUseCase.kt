package com.deencompanion.app.domain.usecase.prayer

import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import javax.inject.Inject

class GetCurrentStreakUseCase @Inject constructor(
    private val repository: PrayerTrackingRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getCurrentStreak()
    }
}
