package com.deencompanion.app.domain.model

/**
 * Domain model representing a verse mapped with its word-by-word breakdown.
 */
data class WordVerse(
    val verseNumber: Int,
    val verseKey: String,
    val arabicTextFull: String,
    val words: List<WordItem>
)

/**
 * Domain model representing an individual word in an ayah with its translation.
 */
data class WordItem(
    val position: Int,
    val arabicText: String,
    val translation: String // Renamed from englishTranslation for multi-language support
)