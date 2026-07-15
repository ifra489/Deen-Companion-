package com.deencompanion.app.domain.repository



import com.deencompanion.app.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun isBookmarked(id: String): Flow<Boolean>

    suspend fun toggleAyahBookmark(
        surahId: Int,
        surahName: String,
        ayahNumber: Int,
        arabicSnippet: String
    )

    suspend fun toggleDuaBookmark(
        duaId: Int,
        title: String,
        snippet: String
    )

    suspend fun removeBookmark(id: String)
}