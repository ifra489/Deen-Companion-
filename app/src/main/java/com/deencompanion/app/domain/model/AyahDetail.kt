package com.deencompanion.app.domain.model



data class AyahDetail(
    val numberInSurah: Int,
    val arabicText: String,
    val translations: Map<String, String> // e.g. "en" -> Translation, "ur" -> Translation, "hi" -> Translation
)