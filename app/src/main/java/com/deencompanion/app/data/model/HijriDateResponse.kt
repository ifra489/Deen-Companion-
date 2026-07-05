package com.deencompanion.app.data.model

data class HijriDateResponse(
    val code: Int,
    val status: String,
    val data: HijriDateData?
)

data class HijriDateData(
    val hijri: HijriInfo?,
    val gregorian: GregorianInfo?
)

data class HijriInfo(
    val day: String,
    val month: HijriMonth?,
    val year: String
)

data class HijriMonth(
    val number: Int,
    val en: String,
    val ar: String
)

data class GregorianInfo(
    val date: String,
    val weekday: GregorianWeekday?
)

data class GregorianWeekday(
    val en: String
)
