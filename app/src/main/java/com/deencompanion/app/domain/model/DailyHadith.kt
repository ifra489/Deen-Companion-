package com.deencompanion.app.domain.model

data class DailyHadith(
    val arabic: String,
    val english: String,
    val urdu: String,
    val romanUrdu: String,
    val reference: String,
    val narrator: String,
    val translation: String,
    val arabicText: String
)
