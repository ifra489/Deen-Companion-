package com.deencompanion.app.presentation.ui.goals


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Goal
import com.deencompanion.app.domain.usecase.*
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase,
    private val incrementGoalProgressUseCase: IncrementGoalProgressUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    val goalsState: StateFlow<UiState<List<Goal>>> = getGoalsUseCase()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "Failed to load goals")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun addGoal(title: String, target: Int) {
        viewModelScope.launch {
            if (title.isNotBlank() && target > 0) addGoalUseCase(title.trim(), target)
        }
    }

    fun incrementProgress(goal: Goal) {
        viewModelScope.launch { incrementGoalProgressUseCase(goal.id, goal.progressCurrent, goal.progressTarget) }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch { deleteGoalUseCase(goalId) }
    }
}