package com.deencompanion.app.data.model


data class WordByWordResponse(
    val verses: List<WordVerseItem>
)

data class WordVerseItem(
    val id: Int,
    val verse_number: Int,
    val verse_key: String,
    val text_uthmani: String,
    val words: List<WordDetails>
)

data class WordDetails(
    val id: Int,
    val position: Int,
    val text_uthmani: String,
    val translation: WordTranslation
)

data class WordTranslation(
    val text: String,
    val language_name: String
)