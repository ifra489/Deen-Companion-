package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.QazaPrayer
import com.deencompanion.app.domain.repository.QazaNamazRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQazaPrayersUseCase @Inject constructor(private val repository: QazaNamazRepository) {
    operator fun invoke(): Flow<List<QazaPrayer>> = repository.getPrayers()
}