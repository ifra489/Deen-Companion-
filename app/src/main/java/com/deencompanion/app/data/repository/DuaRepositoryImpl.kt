package com.deencompanion.app.data.repository



import android.content.Context
import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.domain.repository.DuaRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DuaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DuaRepository {

    private var cachedDuas: List<Dua>? = null

    override suspend fun getAllDuas(): List<Dua> = withContext(Dispatchers.IO) {
        cachedDuas?.let { return@withContext it }

        try {
            val jsonString = context.assets.open("database/dua_database.json")
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val databaseJson = gson.fromJson(jsonString, DuaDatabaseJson::class.java)

            val mappedList = databaseJson.duas.mapIndexed { index, jsonItem ->
                Dua(
                    id = index,
                    arabic = jsonItem.arabic.orEmpty(),
                    transliteration = jsonItem.transliteration.orEmpty(),
                    english = jsonItem.translation_english.orEmpty(),
                    urdu = jsonItem.translation_urdu.orEmpty(),
                    romanUrdu = jsonItem.translation_roman_urdu.orEmpty(),
                    reference = jsonItem.reference.orEmpty(),
                    category = "General"
                )
            }
            cachedDuas = mappedList
            mappedList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getDuaById(id: Int): Dua? {
        return getAllDuas().getOrNull(id)
    }

    private data class DuaDatabaseJson(
        val duas: List<DuaJsonItem>
    )

    private data class DuaJsonItem(
        val arabic: String?,
        val transliteration: String?,
        val translation_english: String?,
        val translation_urdu: String?,
        val translation_roman_urdu: String?,
        val reference: String?
    )
}