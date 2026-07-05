package com.deencompanion.app.domain.usecase

import com.deencompanion.app.domain.model.WordVerse
import com.deencompanion.app.domain.repository.QuranRepository
import javax.inject.Inject

/**
 * Clean Architecture Use Case to fetch word-by-word breakdown of a Surah.
 * Supports multi-language word translations (English, Urdu, Hindi).
 */
class GetWordByWordUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(surahNumber: Int, languageCode: String): Result<List<WordVerse>> {
        return repository.getSurahWordByWord(surahNumber, languageCode)
    }
}