package com.deencompanion.app.domain.usecase

import com.deencompanion.app.domain.model.TasbeehItem
import com.deencompanion.app.domain.repository.TasbeehRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasbeehCountsUseCase @Inject constructor(
    private val repository: TasbeehRepository
) {
    operator fun invoke(): Flow<List<TasbeehItem>> {
        return repository.getAllTasbeehItems()
    }
}