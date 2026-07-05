package com.deencompanion.app.presentation.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.util.UiState
import com.deencompanion.app.util.notification.AdhanScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class AdhanSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adhanScheduler: AdhanScheduler,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val dataStore = context.dataStore

    companion object {
        val ADHAN_NOTIFICATIONS_ENABLED = booleanPreferencesKey("adhan_notifications_enabled")
    }

    val isEnabled: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[ADHAN_NOTIFICATIONS_ENABLED] ?: true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleAdhanNotifications(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[ADHAN_NOTIFICATIONS_ENABLED] = enabled
            }

            if (enabled) {
                val result = homeRepository.getPrayerTimes("Rawalpindi", "Pakistan")
                if (result is UiState.Success) {
                    adhanScheduler.scheduleAllPrayerAlarms(result.data)
                }
            } else {
                adhanScheduler.cancelAllAlarms()
            }
        }
    }
}
