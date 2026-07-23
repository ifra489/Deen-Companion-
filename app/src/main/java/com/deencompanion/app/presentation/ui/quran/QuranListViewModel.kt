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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranListViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase,
    private val quranRepository: com.deencompanion.app.domain.repository.QuranRepository
) : ViewModel() {

    private val _surahListState = MutableStateFlow<UiState<List<Surah>>>(UiState.Loading)
    val surahListState: StateFlow<UiState<List<Surah>>> = _surahListState.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    val downloadedSurahNumbers: StateFlow<List<Int>> = quranRepository.getDownloadedSurahNumbers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadSurahs()
    }

    fun downloadAllSurahs() {
        viewModelScope.launch {
            _downloadProgress.value = 0f
            val total = 114
            for (i in 1..total) {
                try {
                    // Cache text and translations
                    quranRepository.getSurahEditions(i)
                    // Cache English word by word
                    quranRepository.getSurahWordByWord(i, "en")
                    // Cache Audio URLs
                    quranRepository.getSurahAudio(i)
                } catch (_: Exception) { }
                _downloadProgress.value = i.toFloat() / total
            }
            _downloadProgress.value = null
        }
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