package com.deencompanion.app.domain.usecase.home

import com.deencompanion.app.domain.model.HijriDate
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.util.UiState
import javax.inject.Inject

class GetHijriDateUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): UiState<HijriDate> {
        return repository.getHijriDate()
    }
}
