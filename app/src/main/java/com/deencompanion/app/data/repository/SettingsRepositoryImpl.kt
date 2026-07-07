package com.deencompanion.app.data.repository


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.deencompanion.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val darkThemeKey = booleanPreferencesKey("dark_theme_enabled")
    private val defaultLanguageKey = stringPreferencesKey("default_translation_language")

    override val isDarkTheme = dataStore.data.map { prefs -> prefs[darkThemeKey] ?: true }

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[darkThemeKey] = enabled }
    }

    override val defaultTranslationLanguage = dataStore.data.map { prefs -> prefs[defaultLanguageKey] ?: "en" }

    override suspend fun setDefaultTranslationLanguage(language: String) {
        dataStore.edit { it[defaultLanguageKey] = language }
    }
}