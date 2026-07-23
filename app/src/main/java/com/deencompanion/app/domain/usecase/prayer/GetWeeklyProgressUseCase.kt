package com.deencompanion.app.domain.usecase.prayer

import com.deencompanion.app.domain.model.DailyProgress
import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import javax.inject.Inject

class GetWeeklyProgressUseCase @Inject constructor(
    private val repository: PrayerTrackingRepository
) {
    suspend operator fun invoke(): List<DailyProgress> {
        return repository.getWeeklyProgress()
    }
}
