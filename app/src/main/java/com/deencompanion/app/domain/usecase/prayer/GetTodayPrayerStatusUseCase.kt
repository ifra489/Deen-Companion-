package com.deencompanion.app.domain.usecase.prayer

import com.deencompanion.app.domain.model.PrayerRecord
import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayPrayerStatusUseCase @Inject constructor(
    private val repository: PrayerTrackingRepository
) {
    operator fun invoke(date: String): Flow<List<PrayerRecord>> {
        return repository.getTodayPrayerStatus(date)
    }
}
