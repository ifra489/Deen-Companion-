package com.deencompanion.app.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val snippet: String,
    val surahId: Int? = null,
    val ayahNumber: Int? = null,
    val duaId: Int? = null,
    val createdAt: Long
)