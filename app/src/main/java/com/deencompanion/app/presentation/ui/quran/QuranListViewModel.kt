package com.deencompanion.app.presentation.ui.quran



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Surah
import com.deencompanion.app.domain.usecase.GetSurahListUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranListViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase
) : ViewModel() {

    private val _surahListState = MutableStateFlow<UiState<List<Surah>>>(UiState.Loading)
    val surahListState: StateFlow<UiState<List<Surah>>> = _surahListState.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            _surahListState.value = UiState.Loading
            try {
                val surahs = getSurahListUseCase()
                if (surahs.isEmpty()) {
                    _surahListState.value = UiState.Empty
                } else {
                    _surahListState.value = UiState.Success(surahs)
                }
            } catch (e: Exception) {
                _surahListState.value = UiState.Error(e.localizedMessage ?: "An error occurred loading Surahs.")
            }
        }
    }
}