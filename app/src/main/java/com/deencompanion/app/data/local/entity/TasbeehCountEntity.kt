package com.deencompanion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbeeh_counts")
data class TasbeehCountEntity(
    @PrimaryKey val type: String, // "SUBHANALLAH", "ALHAMDULILLAH", "ALLAHU_AKBAR" or "custom_[UUID]"
    val count: Int,
    val lastUpdated: Long,
    val isCustom: Boolean = false,
    val displayName: String = "",
    val targetCount: Int = 33
)