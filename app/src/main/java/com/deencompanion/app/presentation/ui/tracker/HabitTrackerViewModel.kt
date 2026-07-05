package com.deencompanion.app.presentation.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Habit
import com.deencompanion.app.domain.repository.AddCustomHabitUseCase
import com.deencompanion.app.domain.repository.DeleteHabitUseCase
import com.deencompanion.app.domain.usecase.*
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitTrackerViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val toggleHabitUseCase: ToggleHabitUseCase,
    private val addCustomHabitUseCase: AddCustomHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase
) : ViewModel() {

    val habitsState: StateFlow<UiState<List<Habit>>> = getHabitsUseCase()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "Failed to load habits")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun toggleHabit(id: String) {
        viewModelScope.launch { toggleHabitUseCase(id) }
    }

    fun addCustomHabit(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) addCustomHabitUseCase(name.trim())
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch { deleteHabitUseCase(id) }
    }
}