package com.deencompanion.app.domain.usecase



import com.deencompanion.app.domain.model.Habit
import com.deencompanion.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(private val repository: HabitRepository) {
    operator fun invoke(): Flow<List<Habit>> = repository.getHabits()
}