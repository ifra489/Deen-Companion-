package com.deencompanion.app.domain.repository



import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean)
    val defaultTranslationLanguage: Flow<String>
    suspend fun setDefaultTranslationLanguage(language: String)
}