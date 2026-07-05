package com.deencompanion.app.domain.repository

import android.content.Context
import com.deencompanion.app.domain.model.Hadith

interface HadithRepository {
    fun getAllHadiths(context: Context): List<Hadith>
    fun getHadithById(context: Context, id: Int): Hadith?
}