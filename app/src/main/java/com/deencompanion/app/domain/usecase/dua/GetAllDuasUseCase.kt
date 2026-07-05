package com.deencompanion.app.domain.usecase.dua


import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.domain.repository.DuaRepository
import javax.inject.Inject

class GetAllDuasUseCase @Inject constructor(
    private val repository: DuaRepository
) {
    suspend operator fun invoke(): List<Dua> {
        return repository.getAllDuas()
    }
}