package com.deencompanion.app.domain.usecase.home

import com.deencompanion.app.domain.model.Ayah
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.util.UiState
import javax.inject.Inject

class GetDailyAyahUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): UiState<Ayah> {
        return repository.getDailyAyah()
    }
}
