package com.deencompanion.app.domain.model



data class TasbeehItem(
    val id: String, // corresponds to entity 'type'
    val displayName: String,
    val count: Int,
    val lastUpdated: Long,
    val isCustom: Boolean,
    val targetCount: Int
)