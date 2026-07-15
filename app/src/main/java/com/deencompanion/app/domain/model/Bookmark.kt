package com.deencompanion.app.domain.model

data class Bookmark(
    val id: String,
    val type: String, // "ayah" or "dua"
    val title: String,
    val snippet: String,
    val surahId: Int? = null,
    val ayahNumber: Int? = null,
    val duaId: Int? = null,
    val createdAt: Long
)
