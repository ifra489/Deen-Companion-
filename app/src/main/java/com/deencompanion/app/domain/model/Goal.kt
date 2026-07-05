package com.deencompanion.app.domain.model



data class Goal(
    val id: String,
    val title: String,
    val progressCurrent: Int,
    val progressTarget: Int
)