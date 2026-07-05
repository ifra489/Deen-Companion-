package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.QazaSettings
import com.deencompanion.app.domain.repository.QazaNamazRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQazaSettingsUseCase @Inject constructor(private val repository: QazaNamazRepository) {
    operator fun invoke(): Flow<QazaSettings?> = repository.getSettings()
}