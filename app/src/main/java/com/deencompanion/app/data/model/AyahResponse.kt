package com.deencompanion.app.data.model

data class AyahResponse(
    val code: Int,
    val status: String,
    val data: List<AyahEdition>?
)

data class AyahEdition(
    val text: String,
    val numberInSurah: Int,
    val surah: AyahSurah?,
    val edition: AyahEditionInfo?
)

data class AyahSurah(
    val name: String,
    val englishName: String,
    val number: Int
)

data class AyahEditionInfo(
    val identifier: String,
    val language: String
)
