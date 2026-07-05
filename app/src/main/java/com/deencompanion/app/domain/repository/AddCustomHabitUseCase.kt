package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.repository.HabitRepository
import javax.inject.Inject

class AddCustomHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(name: String) = repository.addCustomHabit(name)
}