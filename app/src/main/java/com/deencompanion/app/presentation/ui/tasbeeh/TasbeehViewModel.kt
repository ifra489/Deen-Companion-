package com.deencompanion.app.presentation.ui.tasbeeh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.TasbeehItem
import com.deencompanion.app.domain.usecase.*
import com.deencompanion.app.domain.usecase.prayer.AddCustomTasbeehUseCase
import com.deencompanion.app.domain.usecase.prayer.DeleteCustomTasbeehUseCase
import com.deencompanion.app.domain.usecase.prayer.UpdateTasbeehTargetUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasbeehViewModel @Inject constructor(
    private val getTasbeehCountsUseCase: GetTasbeehCountsUseCase,
    private val incrementTasbeehUseCase: IncrementTasbeehUseCase,
    private val resetTasbeehUseCase: ResetTasbeehUseCase,
    private val addCustomTasbeehUseCase: AddCustomTasbeehUseCase,
    private val deleteCustomTasbeehUseCase: DeleteCustomTasbeehUseCase,
    private val updateTasbeehTargetUseCase: UpdateTasbeehTargetUseCase
) : ViewModel() {

    private val _selectedItemId = MutableStateFlow("SUBHANALLAH")
    val selectedItemId: StateFlow<String> = _selectedItemId.asStateFlow()

    private val _celebrationEvent = MutableSharedFlow<String>()
    val celebrationEvent: SharedFlow<String> = _celebrationEvent.asSharedFlow()

    // Track previously known counts to trigger the target reached celebration exactly once
    private val lastCounts = mutableMapOf<String, Int>()

    val tasbeehItemsState: StateFlow<UiState<List<TasbeehItem>>> = getTasbeehCountsUseCase()
        .map { list ->
            if (list.isEmpty()) {
                UiState.Empty
            } else {
                list.forEach { item ->
                    val prevCount = lastCounts[item.id]
                    if (prevCount != null && prevCount < item.targetCount && item.count >= item.targetCount) {
                        viewModelScope.launch {
                            _celebrationEvent.emit("Target reached: ${item.displayName} ${item.count}/${item.targetCount}!")
                        }
                    }
                    lastCounts[item.id] = item.count
                }
                UiState.Success(list)
            }
        }
        .catch { e ->
            emit(UiState.Error(e.message ?: "An unexpected error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun selectItem(id: String) {
        _selectedItemId.value = id
    }

    fun incrementCount(id: String) {
        viewModelScope.launch {
            incrementTasbeehUseCase(id)
        }
    }

    fun resetCount(id: String) {
        viewModelScope.launch {
            resetTasbeehUseCase(id)
        }
    }

    fun addCustomDhikr(name: String, target: Int = 33) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val trimmedName = name.trim()
                val newId = addCustomTasbeehUseCase(trimmedName, target)
                _selectedItemId.value = newId
            }
        }
    }

    fun deleteCustomDhikr(id: String) {
        viewModelScope.launch {
            if (_selectedItemId.value == id) {
                _selectedItemId.value = "SUBHANALLAH"
            }
            deleteCustomTasbeehUseCase(id)
        }
    }

    fun updateTarget(id: String, target: Int) {
        viewModelScope.launch {
            if (target > 0) {
                updateTasbeehTargetUseCase(id, target)
            }
        }
    }
}