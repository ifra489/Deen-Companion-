package com.deencompanion.app.domain.repository



import android.content.Context
import com.deencompanion.app.domain.model.Azkar

interface AzkarRepository {
    fun getAzkarByType(context: Context, type: String): List<Azkar>
}