package com.deencompanion.app.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.domain.repository.SettingsRepository
import com.deencompanion.app.util.UiState
import com.deencompanion.app.util.notification.AdhanScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdhanSettingsViewModel @Inject constructor(
    private val adhanScheduler: AdhanScheduler,
    private val homeRepository: HomeRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isEnabled: StateFlow<Boolean> = settingsRepository.isAdhanEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleAdhanNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAdhanEnabled(enabled)

            if (enabled) {
                // Use fallback coordinates for Rawalpindi
                val result = homeRepository.getPrayerTimes(33.5973, 73.0479)
                if (result is UiState.Success) {
                    adhanScheduler.scheduleAllPrayerAlarms(result.data)
                }
            } else {
                adhanScheduler.cancelAllAlarms()
            }
        }
    }
}
