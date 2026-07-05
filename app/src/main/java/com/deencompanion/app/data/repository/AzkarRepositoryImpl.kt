package com.deencompanion.app.data.repository



import android.content.Context
import com.deencompanion.app.domain.model.Azkar
import com.deencompanion.app.domain.repository.AzkarRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AzkarRepositoryImpl @Inject constructor() : AzkarRepository {

    private val gson = Gson()

    override fun getAzkarByType(context: Context, type: String): List<Azkar> {
        val database = readJsonFromAssets(context)
        return database.azkars
            .filter { it.type.equals(type, ignoreCase = true) }
            .map {
                Azkar(
                    id = it.id,
                    type = it.type,
                    arabic = it.arabic,
                    english = it.english,
                    urdu = it.urdu,
                    romanUrdu = it.romanUrdu,
                    reference = it.reference,
                    repeatCount = it.repeatCount
                )
            }
    }

    private fun readJsonFromAssets(context: Context): AzkarDatabaseJson {
        context.assets.open(AZKAR_DATABASE_FILE).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                return gson.fromJson(reader, AzkarDatabaseJson::class.java)
            }
        }
    }

    private companion object {
        const val AZKAR_DATABASE_FILE = "database/azkar_database.json"
    }
}

private data class AzkarDatabaseJson(val azkars: List<AzkarJsonItem>)

private data class AzkarJsonItem(
    val id: Int,
    val type: String,
    val arabic: String,
    val english: String,
    val urdu: String,
    @SerializedName("romanUrdu") val romanUrdu: String,
    val reference: String,
    val repeatCount: Int
)