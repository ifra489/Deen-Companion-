package com.deencompanion.app.domain.model



data class Dua(
    val id: Int,
    val arabic: String,
    val transliteration: String,
    val english: String,
    val urdu: String,
    val romanUrdu: String,
    val reference: String,
    val category: String = "General"
)