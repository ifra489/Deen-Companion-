package com.deencompanion.app.data.repository

import com.deencompanion.app.data.local.dao.QuranCacheDao
import com.deencompanion.app.data.model.AudioAyahItem

import com.deencompanion.app.data.local.entity.CachedSurahEntity
import com.deencompanion.app.data.remote.api.QuranApi
import com.deencompanion.app.data.remote.api.QuranWordApi
import com.deencompanion.app.domain.model.AyahDetail
import com.deencompanion.app.domain.model.WordItem
import com.deencompanion.app.domain.model.WordVerse
import com.deencompanion.app.domain.repository.QuranRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val quranApi: QuranApi,
    private val quranWordApi: QuranWordApi,
    private val quranCacheDao: QuranCacheDao,
    private val gson: Gson
) : QuranRepository {

    override fun getDownloadedSurahNumbers(): Flow<List<Int>> {
        return quranCacheDao.getDownloadedSurahNumbers()
    }

    override suspend fun getSurahEditions(surahNumber: Int): Result<List<AyahDetail>> {
        val cacheType = "editions"
        
        // Always try to load from cache first for immediate response
        val cached = quranCacheDao.getCachedData(surahNumber, cacheType)
        if (cached != null) {
            val type = object : TypeToken<List<AyahDetail>>() {}.type
            val cachedData = gson.fromJson<List<AyahDetail>>(cached.jsonData, type)
            if (cachedData.isNotEmpty()) {
                return Result.success(cachedData)
            }
        }

        return try {
            val response = quranApi.getSurahEditions(surahNumber)
            if (response.isSuccessful && response.body() != null) {
                val editionsResponse = response.body()!!
                val editionsList = editionsResponse.data

                val arabicEdition = editionsList.find { it.edition.identifier == "quran-simple" } ?: editionsList.first()
                val urduEdition = editionsList.find { it.edition.identifier == "ur.maududi" }
                val englishEdition = editionsList.find { it.edition.identifier == "en.asad" }
                val hindiEdition = editionsList.find { it.edition.identifier == "hi.hindi" }

                val ayahs = List(arabicEdition.ayahs.size) { i ->
                    val arabicAyah = arabicEdition.ayahs[i]
                    val translationsMap = mutableMapOf<String, String>()
                    urduEdition?.ayahs?.getOrNull(i)?.text?.let { translationsMap["ur"] = it }
                    englishEdition?.ayahs?.getOrNull(i)?.text?.let { translationsMap["en"] = it }
                    hindiEdition?.ayahs?.getOrNull(i)?.text?.let { translationsMap["hi"] = it }

                    AyahDetail(
                        numberInSurah = arabicAyah.numberInSurah,
                        arabicText = arabicAyah.text,
                        translations = translationsMap
                    )
                }

                cacheData(surahNumber, cacheType, ayahs)
                Result.success(ayahs)
            } else {
                loadEditionsFromCache(surahNumber, cacheType)
            }
        } catch (e: Exception) {
            loadEditionsFromCache(surahNumber, cacheType, e)
        }
    }

    override suspend fun getSurahAudio(surahNumber: Int): Result<List<AudioAyahItem>> {
        val cacheType = "audio"
        
        // Try cache first
        val cached = quranCacheDao.getCachedData(surahNumber, cacheType)
        if (cached != null) {
            val type = object : TypeToken<List<AudioAyahItem>>() {}.type
            val cachedData = gson.fromJson<List<AudioAyahItem>>(cached.jsonData, type)
            if (cachedData.isNotEmpty()) {
                return Result.success(cachedData)
            }
        }

        return try {
            val response = quranApi.getSurahAudio(surahNumber)
            if (response.isSuccessful && response.body() != null) {
                val audioAyahs = response.body()!!.data.ayahs
                cacheData(surahNumber, cacheType, audioAyahs)
                Result.success(audioAyahs)
            } else {
                loadAudioFromCache(surahNumber, cacheType)
            }
        } catch (e: Exception) {
            loadAudioFromCache(surahNumber, cacheType, e)
        }
    }

    private suspend fun loadAudioFromCache(surahNumber: Int, dataType: String, error: Exception? = null): Result<List<AudioAyahItem>> {
        val cached = quranCacheDao.getCachedData(surahNumber, dataType)
        return if (cached != null) {
            val type = object : TypeToken<List<AudioAyahItem>>() {}.type
            Result.success(gson.fromJson(cached.jsonData, type))
        } else {
            Result.failure(error ?: IOException("Audio links unavailable offline for this Surah."))
        }
    }

    override suspend fun getSurahWordByWord(surahNumber: Int, languageCode: String): Result<List<WordVerse>> {
        val apiLang = if (languageCode.lowercase() == "ur") "ur" else "en" // Hindi API support nahi, English fallback
        val cacheType = "wordbyword_${languageCode.lowercase()}"

        // Try cache first
        val cached = quranCacheDao.getCachedData(surahNumber, cacheType)
        if (cached != null) {
            val type = object : TypeToken<List<WordVerse>>() {}.type
            val cachedData = gson.fromJson<List<WordVerse>>(cached.jsonData, type)
            if (cachedData.isNotEmpty()) {
                return Result.success(cachedData)
            }
        }

        return try {
            val response = quranWordApi.getWordByWordVerses(chapterNumber = surahNumber, language = apiLang)
            if (response.isSuccessful && response.body() != null) {
                val verses = response.body()!!.verses.map { verseItem ->
                    WordVerse(
                        verseNumber = verseItem.verse_number,
                        verseKey = verseItem.verse_key,
                        arabicTextFull = verseItem.text_uthmani,
                        words = verseItem.words.map { wordDetails ->
                            WordItem(
                                position = wordDetails.position,
                                arabicText = wordDetails.text_uthmani,
                                translation = wordDetails.translation.text
                            )
                        }
                    )
                }
                cacheData(surahNumber, cacheType, verses)
                Result.success(verses)
            } else {
                loadWordByWordFromCache(surahNumber, cacheType)
            }
        } catch (e: Exception) {
            loadWordByWordFromCache(surahNumber, cacheType, e)
        }
    }

    private suspend fun cacheData(surahNumber: Int, dataType: String, data: Any) {
        try {
            val json = gson.toJson(data)
            quranCacheDao.insertOrUpdate(
                CachedSurahEntity(surahNumber = surahNumber, dataType = dataType, jsonData = json)
            )
        } catch (_: Exception) { }
    }

    private suspend fun loadEditionsFromCache(surahNumber: Int, dataType: String, error: Exception? = null): Result<List<AyahDetail>> {
        val cached = quranCacheDao.getCachedData(surahNumber, dataType)
        return if (cached != null) {
            val type = object : TypeToken<List<AyahDetail>>() {}.type
            Result.success(gson.fromJson(cached.jsonData, type))
        } else {
            Result.failure(error ?: IOException("Internet nahi hai aur is Surah ka koi offline data save nahi."))
        }
    }

    private suspend fun loadWordByWordFromCache(surahNumber: Int, dataType: String, error: Exception? = null): Result<List<WordVerse>> {
        val cached = quranCacheDao.getCachedData(surahNumber, dataType)
        return if (cached != null) {
            val type = object : TypeToken<List<WordVerse>>() {}.type
            Result.success(gson.fromJson(cached.jsonData, type))
        } else {
            Result.failure(error ?: IOException("Internet nahi hai aur is Surah ka koi offline data save nahi."))
        }
    }
}