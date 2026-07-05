package com.deencompanion.app.domain.repository

import com.deencompanion.app.data.model.AudioAyahItem
import com.deencompanion.app.domain.model.AyahDetail
import com.deencompanion.app.domain.model.WordVerse

/**
 * Interface representing the Quran Repository.
 * Handles fetching Surah translations, audio recitations, and word-by-word breakdowns.
 */
interface QuranRepository {
    /**
     * Fetches all ayahs of a surah with multiple translations (English, Urdu, Hindi, Arabic).
     * @param surahNumber The Surah number (1-114).
     */
    suspend fun getSurahEditions(surahNumber: Int): Result<List<AyahDetail>>

    /**
     * Fetches per-ayah audio recitation URLs for a surah.
     * @param surahNumber The Surah number (1-114).
     */
    suspend fun getSurahAudio(surahNumber: Int): Result<List<AudioAyahItem>>

    /**
     * Fetches word-by-word translations for a surah in a specific language.
     * @param surahNumber The Surah number (1-114).
     * @param languageCode The language code ("en", "ur", "hi").
     */
    suspend fun getSurahWordByWord(surahNumber: Int, languageCode: String): Result<List<WordVerse>>
}