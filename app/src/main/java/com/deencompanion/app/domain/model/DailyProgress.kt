package com.deencompanion.app.domain.model

import java.time.LocalDate

data class DailyProgress(
    val date: LocalDate,
    val completedCount: Int
)
