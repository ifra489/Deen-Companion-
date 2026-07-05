package com.deencompanion.app.domain.model

data class QazaPrayer(
    val prayerType: String,
    val totalMissed: Int,
    val completedCount: Int
) {
    val remaining: Int get() = (totalMissed - completedCount).coerceAtLeast(0)
    val progress: Float get() = if (totalMissed > 0) completedCount.toFloat() / totalMissed else 0f
}

data class QazaSettings(
    val currentAge: Int,
    val obligationAge: Int,
    val ageStartedPraying: Int
)