package com.deencompanion.app.presentation.ui.hijri

import androidx.lifecycle.ViewModel
import com.deencompanion.app.domain.model.HijriCalendarDay
import com.deencompanion.app.domain.model.IslamicEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale
import javax.inject.Inject

data class HijriCalendarUiState(
    val hijriYear: Int = 0,
    val hijriMonth: Int = 1,
    val monthNameEn: String = "",
    val monthNameAr: String = "",
    val days: List<HijriCalendarDay?> = emptyList(),
    val events: List<IslamicEvent> = emptyList()
)

@HiltViewModel
class HijriCalendarViewModel @Inject constructor() : ViewModel() {

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

    private val _uiState = MutableStateFlow(HijriCalendarUiState())
    val uiState: StateFlow<HijriCalendarUiState> = _uiState.asStateFlow()

    init {
        val today = HijrahDate.now()
        loadMonth(today.get(ChronoField.YEAR), today.get(ChronoField.MONTH_OF_YEAR))
    }

    fun goToPreviousMonth() {
        val current = _uiState.value
        if (current.hijriMonth == 1) {
            loadMonth(current.hijriYear - 1, 12)
        } else {
            loadMonth(current.hijriYear, current.hijriMonth - 1)
        }
    }

    fun goToNextMonth() {
        val current = _uiState.value
        if (current.hijriMonth == 12) {
            loadMonth(current.hijriYear + 1, 1)
        } else {
            loadMonth(current.hijriYear, current.hijriMonth + 1)
        }
    }

    private fun loadMonth(year: Int, month: Int) {
        val firstOfMonth = HijrahDate.of(year, month, 1)
        val lengthOfMonth = firstOfMonth.lengthOfMonth()
        val firstGregorian = LocalDate.from(firstOfMonth)

        // Sunday = 0, Monday = 1 ... Saturday = 6 (grid starts on Sunday)
        val leadingEmptyCells = firstGregorian.dayOfWeek.value % 7

        val todayHijri = HijrahDate.now()
        val todayYear = todayHijri.get(ChronoField.YEAR)
        val todayMonth = todayHijri.get(ChronoField.MONTH_OF_YEAR)
        val todayDay = todayHijri.get(ChronoField.DAY_OF_MONTH)

        val days = mutableListOf<HijriCalendarDay?>()
        repeat(leadingEmptyCells) { days.add(null) }

        for (day in 1..lengthOfMonth) {
            val hijriDate = HijrahDate.of(year, month, day)
            val gregorianDate = LocalDate.from(hijriDate)
            val isToday = year == todayYear && month == todayMonth && day == todayDay
            days.add(HijriCalendarDay(hijriDay = day, gregorianDate = gregorianDate, isToday = isToday))
        }

        _uiState.value = HijriCalendarUiState(
            hijriYear = year,
            hijriMonth = month,
            monthNameEn = hijriMonthsEn[month - 1],
            monthNameAr = hijriMonthsAr[month - 1],
            days = days,
            events = getIslamicEventsForYear(year)
        )
    }

    private fun getIslamicEventsForYear(year: Int): List<IslamicEvent> {
        val displayFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

        val ashura = HijrahDate.of(year, 1, 10)
        val ramadanStart = HijrahDate.of(year, 9, 1)
        val eidUlFitr = HijrahDate.of(year, 10, 1)
        val eidUlAdha = HijrahDate.of(year, 12, 10)

        return listOf(
            IslamicEvent("Ashura", "10 Muharram $year AH", LocalDate.from(ashura).also {}.let { LocalDate.from(ashura) }),
            IslamicEvent("Start of Ramadan", "1 Ramadan $year AH", LocalDate.from(ramadanStart)),
            IslamicEvent("Eid ul Fitr", "1 Shawwal $year AH", LocalDate.from(eidUlFitr)),
            IslamicEvent("Eid ul Adha", "10 Dhu al Hijjah $year AH", LocalDate.from(eidUlAdha))
        ).map {
            it.copy() // dates already computed
        }.sortedBy { it.gregorianDate }
    }
}