package com.deencompanion.app.domain.usecase.dua



import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.domain.repository.DuaRepository
import javax.inject.Inject

class GetDuaByIdUseCase @Inject constructor(
    private val repository: DuaRepository
) {
    suspend operator fun invoke(id: Int): Dua? {
        return repository.getDuaById(id)
    }
}