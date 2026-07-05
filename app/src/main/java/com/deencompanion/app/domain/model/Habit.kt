package com.deencompanion.app.domain.model



data class Habit(
    val id: String,
    val name: String,
    val isCustom: Boolean,
    val isCompletedToday: Boolean,
    val currentStreak: Int
)