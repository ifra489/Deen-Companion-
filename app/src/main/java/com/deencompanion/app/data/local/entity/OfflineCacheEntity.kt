package com.deencompanion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_cache")
data class OfflineCacheEntity(
    @PrimaryKey val key: String,
    val jsonData: String,
    val lastCachedAt: Long = System.currentTimeMillis()
)
