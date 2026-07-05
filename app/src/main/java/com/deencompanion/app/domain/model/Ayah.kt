package com.deencompanion.app.domain.model

data class Ayah(
    val arabic: String,
    val english: String,
    val urdu: String,
    val surahName: String,
    val ayahNumber: Int,
    val translation: String,
    val arabicText: String
)
