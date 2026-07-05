package com.deencompanion.app.domain.usecase.prayer

import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import javax.inject.Inject

class MarkPrayerStatusUseCase @Inject constructor(
    private val repository: PrayerTrackingRepository
) {
    suspend operator fun invoke(date: String, prayerName: String, isPrayed: Boolean) {
        repository.markPrayerStatus(date, prayerName, isPrayed)
    }
}