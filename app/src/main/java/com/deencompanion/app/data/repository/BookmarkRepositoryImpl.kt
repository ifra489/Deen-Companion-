package com.deencompanion.app.data.repository

import com.deencompanion.app.domain.model.Bookmark
import com.deencompanion.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val dao: com.deencompanion.app.data.local.dao.BookmarkDao
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return dao.getAllBookmarks().map { list ->
            list.map {
                Bookmark(
                    id = it.id,
                    type = it.type,
                    title = it.title,
                    snippet = it.snippet,
                    surahId = it.surahId,
                    ayahNumber = it.ayahNumber,
                    duaId = it.duaId,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override fun isBookmarked(id: String): Flow<Boolean> = dao.isBookmarked(id).map { it > 0 }

    override suspend fun toggleAyahBookmark(
        surahId: Int,
        surahName: String,
        ayahNumber: Int,
        arabicSnippet: String
    ) {
        val id = ayahBookmarkId(surahId, ayahNumber)
        val alreadyBookmarked = dao.isBookmarked(id).first() > 0
        if (alreadyBookmarked) {
            dao.deleteById(id)
        } else {
            dao.insert(
                com.deencompanion.app.data.local.entity.BookmarkEntity(
                    id = id,
                    type = "ayah",
                    title = "$surahName • Ayah $ayahNumber",
                    snippet = arabicSnippet.take(80),
                    surahId = surahId,
                    ayahNumber = ayahNumber,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun toggleDuaBookmark(duaId: Int, title: String, snippet: String) {
        val id = duaBookmarkId(duaId)
        val alreadyBookmarked = dao.isBookmarked(id).first() > 0
        if (alreadyBookmarked) {
            dao.deleteById(id)
        } else {
            dao.insert(
                com.deencompanion.app.data.local.entity.BookmarkEntity(
                    id = id,
                    type = "dua",
                    title = title,
                    snippet = snippet.take(80),
                    duaId = duaId,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun removeBookmark(id: String) {
        dao.deleteById(id)
    }

    companion object {
        fun ayahBookmarkId(surahId: Int, ayahNumber: Int) = "ayah_${surahId}_$ayahNumber"
        fun duaBookmarkId(duaId: Int) = "dua_$duaId"
    }
}