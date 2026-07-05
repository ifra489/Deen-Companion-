package com.deencompanion.app.data.model



data class AudioSurahResponse(
    val code: Int,
    val status: String,
    val data: AudioSurahData
)

data class AudioSurahData(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<AudioAyahItem>
)

data class AudioAyahItem(
    val number: Int,
    val audio: String,
    val audioSecondary: List<String>,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: Boolean
)