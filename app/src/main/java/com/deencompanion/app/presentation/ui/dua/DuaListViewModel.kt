package com.deencompanion.app.presentation.ui.dua



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.domain.usecase.dua.GetAllDuasUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaListViewModel @Inject constructor(
    private val getAllDuasUseCase: GetAllDuasUseCase
) : ViewModel() {

    private val _duaListState = MutableStateFlow<UiState<List<Dua>>>(UiState.Loading)

    val duaListState: StateFlow<UiState<List<Dua>>> = _duaListState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredDuas: StateFlow<List<Dua>> = combine(duaListState, searchQuery) { state, query ->
        when (state) {
            is UiState.Success -> {
                if (query.isBlank()) {
                    state.data
                } else {
                    state.data.filter { dua ->
                        dua.transliteration.contains(query, ignoreCase = true) ||
                                dua.english.contains(query, ignoreCase = true)
                    }
                }
            }
            else -> emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadDuas()
    }

    fun loadDuas() {
        viewModelScope.launch {
            _duaListState.value = UiState.Loading
            try {
                val duas = getAllDuasUseCase()
                if (duas.isEmpty()) {
                    _duaListState.value = UiState.Empty
                } else {
                    _duaListState.value = UiState.Success(duas)
                }
            } catch (e: Exception) {
                _duaListState.value = UiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}