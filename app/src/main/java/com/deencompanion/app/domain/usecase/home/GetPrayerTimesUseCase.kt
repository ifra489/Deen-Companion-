package com.deencompanion.app.domain.usecase.home

import com.deencompanion.app.domain.model.PrayerTimes
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.util.UiState
import javax.inject.Inject

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): UiState<PrayerTimes> {
        return repository.getPrayerTimes(latitude, longitude)
    }
}
