package com.deencompanion.app.domain.usecase

import android.content.Context
import com.deencompanion.app.domain.model.Hadith
import com.deencompanion.app.domain.repository.HadithRepository
import javax.inject.Inject

class GetAllHadithsUseCase @Inject constructor(
    private val repository: HadithRepository
) {
    operator fun invoke(context: Context): List<Hadith> {
        return repository.getAllHadiths(context)
    }
}