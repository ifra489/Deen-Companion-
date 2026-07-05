package com.deencompanion.app.data.model


data class SurahEditionsResponse(
    val code: Int,
    val status: String,
    val data: List<SurahEditionData>
)

data class SurahEditionData(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<AyahEditionItem>,
    val edition: EditionInfo
)

data class AyahEditionItem(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: Any
)

data class EditionInfo(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String,
    val direction: String
)