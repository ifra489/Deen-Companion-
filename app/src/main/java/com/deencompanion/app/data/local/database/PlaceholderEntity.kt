package com.deencompanion.app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placeholder_table")
data class PlaceholderEntity(
    @PrimaryKey
    val id: Int = 1,
    val value: String = "placeholder"
)
