package com.deencompanion.app.presentation.ui.dua



import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.domain.usecase.dua.GetDuaByIdUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaDetailViewModel @Inject constructor(
    private val getDuaByIdUseCase: GetDuaByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _duaState = MutableStateFlow<UiState<Dua>>(UiState.Loading)
    val duaState: StateFlow<UiState<Dua>> = _duaState

    init {
        val duaId = savedStateHandle.get<Int>("duaId")
            ?: savedStateHandle.get<String>("duaId")?.toIntOrNull()

        if (duaId != null) {
            loadDua(duaId)
        } else {
            _duaState.value = UiState.Error("Invalid Dua ID")
        }
    }

    fun loadDua(id: Int) {
        viewModelScope.launch {
            _duaState.value = UiState.Loading
            try {
                val dua = getDuaByIdUseCase(id)
                if (dua != null) {
                    _duaState.value = UiState.Success(dua)
                } else {
                    _duaState.value = UiState.Error("Dua not found")
                }
            } catch (e: Exception) {
                _duaState.value = UiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}