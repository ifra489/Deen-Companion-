package com.deencompanion.app.domain.model


data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val currentValue: Int,
    val targetValue: Int,
    val isUnlocked: Boolean
) {
    val progress: Float get() = if (targetValue > 0) (currentValue.toFloat() / targetValue).coerceIn(0f, 1f) else 0f
}