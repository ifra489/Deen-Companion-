package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.AyahDetail
import com.deencompanion.app.domain.repository.QuranRepository
import javax.inject.Inject

class GetSurahDetailUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(surahNumber: Int): Result<List<AyahDetail>> {
        return repository.getSurahEditions(surahNumber)
    }
}