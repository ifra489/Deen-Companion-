package com.deencompanion.app.data.repository

import android.content.Context
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.data.DateComponents
import com.batoulapps.adhan.PrayerTimes as AdhanPrayerTimes
import com.deencompanion.app.data.local.dao.OfflineCacheDao
import com.deencompanion.app.data.local.entity.OfflineCacheEntity
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
    private val quranApi: QuranApi,
    private val offlineCacheDao: OfflineCacheDao
) : HomeRepository {

    private val gson = Gson()
    private val apiDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val gregorianDisplayFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    override suspend fun getPrayerTimes(latitude: Double, longitude: Double): UiState<PrayerTimes> =
        withContext(Dispatchers.IO) {
            val cacheKey = "prayer_times"
            try {
                val coordinates = Coordinates(latitude, longitude)
                val calendar = Calendar.getInstance()
                val dateComponents = DateComponents(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
                val adhanTimes = AdhanPrayerTimes(coordinates, dateComponents, params)

                val fajr = formatAdhanTime(adhanTimes.fajr)
                val dhuhr = formatAdhanTime(adhanTimes.dhuhr)
                val asr = formatAdhanTime(adhanTimes.asr)
                val maghrib = formatAdhanTime(adhanTimes.maghrib)
                val isha = formatAdhanTime(adhanTimes.isha)

                val (nextPrayerName, nextPrayerTime) = calculateNextPrayer(
                    fajr = fajr,
                    dhuhr = dhuhr,
                    asr = asr,
                    maghrib = maghrib,
                    isha = isha
                )

                val prayerTimes = PrayerTimes(
                    fajr = fajr,
                    dhuhr = dhuhr,
                    asr = asr,
                    maghrib = maghrib,
                    isha = isha,
                    nextPrayerName = nextPrayerName,
                    nextPrayerTime = nextPrayerTime
                )

                // Cache prayer times
                offlineCacheDao.insertOrUpdate(OfflineCacheEntity(cacheKey, gson.toJson(prayerTimes)))

                UiState.Success(prayerTimes)
            } catch (e: Exception) {
                // Try to load from cache
                val cached = offlineCacheDao.getCachedData(cacheKey)
                if (cached != null) {
                    UiState.Success(gson.fromJson(cached.jsonData, PrayerTimes::class.java))
                } else {
                    UiState.Error(e.message ?: "Failed to calculate prayer times")
                }
            }
        }

    private fun formatAdhanTime(date: java.util.Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        return String.format(Locale.US, "%02d:%02d", hours, minutes)
    }

    override suspend fun getHijriDate(): UiState<HijriDate> = withContext(Dispatchers.IO) {
        val cacheKey = "hijri_date"
        try {
            val today = LocalDate.now().format(apiDateFormatter)
            val response = aladhanApi.getHijriDate(date = today)

            if (response.isSuccessful) {
                val data = response.body()?.data
                val hijri = data?.hijri
                if (hijri != null) {
                    val hijriDate = HijriDate(
                        hijriDay = hijri.day,
                        hijriMonth = hijri.month?.en.orEmpty(),
                        hijriMonthArabic = hijri.month?.ar.orEmpty(),
                        hijriYear = hijri.year,
                        gregorianFormatted = formatGregorianDate(
                            weekday = data.gregorian?.weekday?.en.orEmpty(),
                            date = data.gregorian?.date.orEmpty()
                        )
                    )
                    // Cache hijri date
                    offlineCacheDao.insertOrUpdate(OfflineCacheEntity(cacheKey, gson.toJson(hijriDate)))
                    return@withContext UiState.Success(hijriDate)
                }
            }
            
            // Try to load from cache first before local calculation
            val cached = offlineCacheDao.getCachedData(cacheKey)
            if (cached != null) {
                UiState.Success(gson.fromJson(cached.jsonData, HijriDate::class.java))
            } else {
                getHijriDateLocal()
            }
        } catch (e: Exception) {
            val cached = offlineCacheDao.getCachedData(cacheKey)
            if (cached != null) {
                UiState.Success(gson.fromJson(cached.jsonData, HijriDate::class.java))
            } else {
                getHijriDateLocal()
            }
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
        val cacheKey = "daily_ayah"
        try {
            val response = quranApi.getRandomAyah()

            if (response.isSuccessful) {
                val editions = response.body()?.data
                if (!editions.isNullOrEmpty() && editions.size >= 3) {
                    val arabicEdition = editions[0]
                    val englishEdition = editions[1]
                    val urduEdition = editions[2]

                    val ayah = Ayah(
                        arabic = arabicEdition.text,
                        english = englishEdition.text,
                        urdu = urduEdition.text,
                        surahName = arabicEdition.surah?.englishName.orEmpty(),
                        ayahNumber = arabicEdition.numberInSurah,
                        translation = englishEdition.text,
                        arabicText = arabicEdition.text
                    )
                    // Cache daily ayah
                    offlineCacheDao.insertOrUpdate(OfflineCacheEntity(cacheKey, gson.toJson(ayah)))
                    return@withContext UiState.Success(ayah)
                }
            }
            
            // Try cache if API fails
            val cached = offlineCacheDao.getCachedData(cacheKey)
            if (cached != null) {
                UiState.Success(gson.fromJson(cached.jsonData, Ayah::class.java))
            } else {
                // Fallback Ayah if nothing cached
                UiState.Success(Ayah(
                    arabic = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ",
                    english = "And your Lord says, \"Call upon Me; I will respond to you.\"",
                    urdu = "اور تمہارے پروردگار نے ارشاد فرمایا ہے کہ مجھ سے دعا کرو، میں تمہاری (دعا) قبول کروں گا",
                    surahName = "Ghafir",
                    ayahNumber = 60,
                    translation = "And your Lord says, \"Call upon Me; I will respond to you.\"",
                    arabicText = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ"
                ))
            }
        } catch (e: Exception) {
            val cached = offlineCacheDao.getCachedData(cacheKey)
            if (cached != null) {
                UiState.Success(gson.fromJson(cached.jsonData, Ayah::class.java))
            } else {
                // Fallback Ayah if nothing cached
                UiState.Success(Ayah(
                    arabic = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ",
                    english = "And your Lord says, \"Call upon Me; I will respond to you.\"",
                    urdu = "اور تمہارے پروردگار نے ارشاد فرمایا ہے کہ مجھ سے دعا کرو، میں تمہاری (دعا) قبول کروں گا",
                    surahName = "Ghafir",
                    ayahNumber = 60,
                    translation = "And your Lord says, \"Call upon Me; I will respond to you.\"",
                    arabicText = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ"
                ))
            }
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
