package com.deencompanion.app.domain.usecase


import com.deencompanion.app.domain.repository.HabitRepository
import javax.inject.Inject

class ToggleHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habitId: String) = repository.toggleTodayCompletion(habitId)
}