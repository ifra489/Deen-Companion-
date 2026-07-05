package com.deencompanion.app.presentation.ui.azkar



import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Azkar
import com.deencompanion.app.domain.usecase.GetAzkarByTypeUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AzkarDetailViewModel @Inject constructor(
    private val getAzkarByTypeUseCase: GetAzkarByTypeUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val type: String = checkNotNull(savedStateHandle["type"])

    private val _azkarState = MutableStateFlow<UiState<List<Azkar>>>(UiState.Loading)
    val azkarState: StateFlow<UiState<List<Azkar>>> = _azkarState.asStateFlow()

    private val _tallies = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val tallies: StateFlow<Map<Int, Int>> = _tallies.asStateFlow()

    init {
        loadAzkar()
    }

    fun loadAzkar() {
        viewModelScope.launch {
            _azkarState.value = UiState.Loading
            val list = getAzkarByTypeUseCase(context, type)
            _azkarState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }
    }

    fun onTap(azkarId: Int, target: Int) {
        val current = _tallies.value[azkarId] ?: 0
        if (current < target) {
            _tallies.value = _tallies.value.toMutableMap().apply { put(azkarId, current + 1) }
        }
    }

    fun resetTally(azkarId: Int) {
        _tallies.value = _tallies.value.toMutableMap().apply { put(azkarId, 0) }
    }
}