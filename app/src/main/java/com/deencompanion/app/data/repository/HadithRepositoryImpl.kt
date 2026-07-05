package com.deencompanion.app.data.repository

import android.content.Context
import com.deencompanion.app.domain.model.Hadith
import com.deencompanion.app.domain.repository.HadithRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class HadithRepositoryImpl @Inject constructor() : HadithRepository {

    private val gson = Gson()

    override fun getAllHadiths(context: Context): List<Hadith> {
        val database = readJsonFromAssets(context)
        return database.hadiths.map {
            Hadith(
                id = it.id,
                category = it.category,
                source = it.source,
                reference = it.reference,
                narrator = it.narrator,
                arabic = it.arabic,
                english = it.english,
                urdu = it.urdu,
                romanUrdu = it.romanUrdu
            )
        }
    }

    override fun getHadithById(context: Context, id: Int): Hadith? {
        return getAllHadiths(context).find { it.id == id }
    }

    private fun readJsonFromAssets(context: Context): HadithDatabaseJson {
        context.assets.open(HADITH_DATABASE_FILE).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                return gson.fromJson(reader, HadithDatabaseJson::class.java)
            }
        }
    }

    private data class HadithDatabaseJson(
        val hadiths: List<HadithJsonItem>
    )

    private data class HadithJsonItem(
        val id: Int,
        val category: List<String>,
        val source: String,
        val reference: String,
        val narrator: String,
        val arabic: String,
        val english: String,
        val urdu: String,
        @SerializedName("roman_urdu") val romanUrdu: String
    )

    private companion object {
        const val HADITH_DATABASE_FILE = "database/hadith_database.json"
    }
}