

package com.deencompanion.app.domain.model

import java.time.LocalDate

data class HijriCalendarDay(
    val hijriDay: Int,
    val gregorianDate: LocalDate,
    val isToday: Boolean
)

data class IslamicEvent(
    val name: String,
    val hijriDate: String,
    val gregorianDate: LocalDate
)