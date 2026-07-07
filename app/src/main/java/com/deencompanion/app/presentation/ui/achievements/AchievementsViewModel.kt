package com.deencompanion.app.presentation.ui.achievements


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Achievement
import com.deencompanion.app.domain.usecase.GetAchievementsUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    getAchievementsUseCase: GetAchievementsUseCase
) : ViewModel() {

    val achievementsState: StateFlow<UiState<List<Achievement>>> = getAchievementsUseCase()
        .map { UiState.Success(it) as UiState<List<Achievement>> }
        .catch { e -> emit(UiState.Error(e.message ?: "Failed to load achievements")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
}