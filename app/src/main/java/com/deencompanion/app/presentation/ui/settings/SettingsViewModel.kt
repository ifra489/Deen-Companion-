package com.deencompanion.app.presentation.ui.settings


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val defaultTranslationLanguage: StateFlow<String> = settingsRepository.defaultTranslationLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkTheme(enabled) }
    }

    fun setDefaultTranslationLanguage(language: String) {
        viewModelScope.launch { settingsRepository.setDefaultTranslationLanguage(language) }
    }
}