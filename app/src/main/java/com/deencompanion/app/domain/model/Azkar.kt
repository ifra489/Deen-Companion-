package com.deencompanion.app.domain.model


data class Azkar(
    val id: Int,
    val type: String,
    val arabic: String,
    val english: String,
    val urdu: String,
    val romanUrdu: String,
    val reference: String,
    val repeatCount: Int
)