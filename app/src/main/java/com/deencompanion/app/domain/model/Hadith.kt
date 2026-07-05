package com.deencompanion.app.domain.model


data class Hadith(
    val id: Int,
    val category: List<String>,
    val source: String,
    val reference: String,
    val narrator: String,
    val arabic: String,
    val english: String,
    val urdu: String,
    val romanUrdu: String
)