package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: String) = repository.deleteHabit(habitId)
}