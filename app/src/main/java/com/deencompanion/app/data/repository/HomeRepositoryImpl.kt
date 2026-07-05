package com.deencompanion.app.data.repository

import android.content.Context
import com.deencompanion.app.data.remote.api.AladhanApi
import com.deencompanion.app.data.remote.api.QuranApi
import com.deencompanion.app.domain.model.Ayah
import com.deencompanion.app.domain.model.DailyDua
import com.deencompanion.app.domain.model.DailyHadith
import com.deencompanion.app.domain.model.HijriDate
import com.deencompanion.app.domain.model.PrayerTimes
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.util.UiState
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val aladhanApi: AladhanApi,
    private val quranApi: QuranApi
) : HomeRepository {

    private val gson = Gson()
    private val apiDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val gregorianDisplayFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    override suspend fun getPrayerTimes(city: String, country: String): UiState<PrayerTimes> =
        withContext(Dispatchers.IO) {
            try {
                val response = aladhanApi.getPrayerTimes(city = city, country = country)
                if (!response.isSuccessful) {
                    return@withContext UiState.Error("Failed to load prayer times")
                }

                val timings = response.body()?.data?.timings
                    ?: return@withContext UiState.Error("Failed to load prayer times")

                val fajr = cleanTime(timings.fajr)
                val dhuhr = cleanTime(timings.dhuhr)
                val asr = cleanTime(timings.asr)
                val maghrib = cleanTime(timings.maghrib)
                val isha = cleanTime(timings.isha)

                val (nextPrayerName, nextPrayerTime) = calculateNextPrayer(
                    fajr = fajr,
                    dhuhr = dhuhr,
                    asr = asr,
                    maghrib = maghrib,
                    isha = isha
                )

                UiState.Success(
                    PrayerTimes(
                        fajr = fajr,
                        dhuhr = dhuhr,
                        asr = asr,
                        maghrib = maghrib,
                        isha = isha,
                        nextPrayerName = nextPrayerName,
                        nextPrayerTime = nextPrayerTime
                    )
                )
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load prayer times")
            }
        }

    override suspend fun getHijriDate(): UiState<HijriDate> = withContext(Dispatchers.IO) {
        try {
            val today = LocalDate.now().format(apiDateFormatter)
            val response = aladhanApi.getHijriDate(date = today)

            if (response.isSuccessful) {
                val data = response.body()?.data
                val hijri = data?.hijri
                if (hijri != null) {
                    return@withContext UiState.Success(
                        HijriDate(
                            hijriDay = hijri.day,
                            hijriMonth = hijri.month?.en.orEmpty(),
                            hijriMonthArabic = hijri.month?.ar.orEmpty(),
                            hijriYear = hijri.year,
                            gregorianFormatted = formatGregorianDate(
                                weekday = data.gregorian?.weekday?.en.orEmpty(),
                                date = data.gregorian?.date.orEmpty()
                            )
                        )
                    )
                }
            }
            
            // Fallback to local calculation if API fails
            getHijriDateLocal()
        } catch (e: Exception) {
            getHijriDateLocal()
        }
    }

    /**
     * Fallback method to calculate Hijri date locally using java.time.chrono.HijrahDate
     * when the Aladhan API is unavailable.
     */
    private fun getHijriDateLocal(): UiState<HijriDate> {
        return try {
            val hijrahDate = HijrahDate.now()
            val day = hijrahDate.get(ChronoField.DAY_OF_MONTH)
            val month = hijrahDate.get(ChronoField.MONTH_OF_YEAR)
            val year = hijrahDate.get(ChronoField.YEAR)

            val monthEn = hijriMonthsEn.getOrElse(month - 1) { "" }
            val monthAr = hijriMonthsAr.getOrElse(month - 1) { "" }

            val localDate = LocalDate.now()
            val weekday = localDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))
            val dateFormatted = localDate.format(gregorianDisplayFormatter)

            UiState.Success(
                HijriDate(
                    hijriDay = day.toString(),
                    hijriMonth = monthEn,
                    hijriMonthArabic = monthAr,
                    hijriYear = year.toString(),
                    gregorianFormatted = "$weekday, $dateFormatted"
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to load Hijri date")
        }
    }

    override suspend fun getDailyAyah(): UiState<Ayah> = withContext(Dispatchers.IO) {
        try {
            val response = quranApi.getRandomAyah()

            if (!response.isSuccessful) {
                return@withContext UiState.Error("Failed to load daily ayah")
            }

            val editions = response.body()?.data
            if (editions.isNullOrEmpty() || editions.size < 3) {
                return@withContext UiState.Error("Failed to load daily ayah")
            }

            val arabicEdition = editions[0]
            val englishEdition = editions[1]
            val urduEdition = editions[2]

            UiState.Success(
                Ayah(
                    arabic = arabicEdition.text,
                    english = englishEdition.text,
                    urdu = urduEdition.text,
                    surahName = arabicEdition.surah?.englishName.orEmpty(),
                    ayahNumber = arabicEdition.numberInSurah,
                    translation = englishEdition.text,
                    arabicText = arabicEdition.text
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to load daily ayah")
        }
    }

    override fun getDailyHadith(context: Context): DailyHadith {
        val database = readJsonFromAssets<HadithDatabaseJson>(
            context = context,
            fileName = HADITH_DATABASE_FILE
        )
        val hadiths = database.hadiths
        val index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % hadiths.size
        val hadith = hadiths[index]

        return DailyHadith(
            arabic = hadith.arabic,
            english = hadith.english,
            urdu = hadith.urdu,
            romanUrdu = hadith.romanUrdu,
            reference = hadith.reference,
            narrator = hadith.narrator,
            translation = hadith.english,
            arabicText = hadith.arabic
        )
    }

    override fun getDailyDua(context: Context): DailyDua {
        val database = readJsonFromAssets<DuaDatabaseJson>(
            context = context,
            fileName = DUA_DATABASE_FILE
        )
        val duas = database.duas
        val index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % duas.size
        val dua = duas[index]

        return DailyDua(
            arabic = dua.arabic,
            transliteration = dua.transliteration,
            english = dua.translationEnglish,
            urdu = dua.translationUrdu,
            romanUrdu = dua.translationRomanUrdu,
            reference = dua.reference,
            translation = dua.translationEnglish,
            arabicText = dua.arabic
        )
    }

    private fun cleanTime(rawTime: String): String {
        return rawTime.substringBefore(" ").trim()
    }

    private fun calculateNextPrayer(
        fajr: String,
        dhuhr: String,
        asr: String,
        maghrib: String,
        isha: String
    ): Pair<String, String> {
        val now = LocalTime.now()
        val prayers = listOf(
            "Fajr" to fajr,
            "Dhuhr" to dhuhr,
            "Asr" to asr,
            "Maghrib" to maghrib,
            "Isha" to isha
        )

        for ((name, time) in prayers) {
            val prayerTime = LocalTime.parse(time, timeFormatter)
            if (prayerTime.isAfter(now)) {
                return name to time
            }
        }

        return "Fajr" to fajr
    }

    private fun formatGregorianDate(weekday: String, date: String): String {
        if (date.isBlank()) return weekday

        return try {
            val parsedDate = LocalDate.parse(date, apiDateFormatter)
            val formattedDate = parsedDate.format(gregorianDisplayFormatter)
            if (weekday.isBlank()) formattedDate else "$weekday, $formattedDate"
        } catch (_: Exception) {
            weekday.ifBlank { date }
        }
    }

    private inline fun <reified T> readJsonFromAssets(context: Context, fileName: String): T {
        context.assets.open(fileName).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                return gson.fromJson(reader, T::class.java)
            }
        }
    }

    private companion object {
        const val HADITH_DATABASE_FILE = "database/hadith_database.json"
        const val DUA_DATABASE_FILE = "database/dua_database.json"

        private val hijriMonthsEn = listOf(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-ula", "Jumada al-akhira", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        private val hijriMonthsAr = listOf(
            "المُحَرَّم", "صَفَر", "رَبيع الأوّل", "رَبيع الثاني",
            "جُمادى الأولى", "جُمادى الآخِرة", "رَجَب", "شَعْبان",
            "رَمَضان", "شَوّال", "ذو القعدة", "ذو الحجة"
        )
    }

    private data class HadithDatabaseJson(
        val hadiths: List<HadithJsonItem>
    )

    private data class HadithJsonItem(
        val arabic: String,
        val english: String,
        val urdu: String,
        @SerializedName("roman_urdu") val romanUrdu: String,
        val reference: String,
        val narrator: String
    )

    private data class DuaDatabaseJson(
        val duas: List<DuaJsonItem>
    )

    private data class DuaJsonItem(
        val arabic: String,
        val transliteration: String,
        @SerializedName("translation_english") val translationEnglish: String,
        @SerializedName("translation_urdu") val translationUrdu: String,
        @SerializedName("translation_roman_urdu") val translationRomanUrdu: String,
        val reference: String
    )
}
