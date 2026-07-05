package com.deencompanion.app.domain.model

enum class TasbeehType {
    SUBHANALLAH,
    ALHAMDULILLAH,
    ALLAHU_AKBAR;

    fun getDisplayName(): String {
        return when (this) {
            SUBHANALLAH -> "SubhanAllah"
            ALHAMDULILLAH -> "Alhamdulillah"
            ALLAHU_AKBAR -> "Allahu Akbar"
        }
    }
}