package com.deencompanion.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for caching Surah data (editions, translations, word-by-word) as serialized JSON.
 */
@Entity(
    tableName = "cached_surahs",
    primaryKeys = ["surahNumber", "dataType"]
)
data class CachedSurahEntity(
    val surahNumber: Int,
    val dataType: String, // e.g. "editions" or "wordbyword_en", "wordbyword_ur", "wordbyword_hi"
    val jsonData: String, // Serialized JSON payload using Gson
    val lastCachedAt: Long = System.currentTimeMillis()
)
