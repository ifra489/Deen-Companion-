package com.deencompanion.app.presentation.ui.quran

import android.media.MediaPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.data.model.AudioAyahItem
import com.deencompanion.app.domain.model.AyahDetail
import com.deencompanion.app.domain.model.WordVerse
import com.deencompanion.app.domain.usecase.GetSurahDetailUseCase
import com.deencompanion.app.domain.usecase.GetWordByWordUseCase
import com.deencompanion.app.domain.repository.QuranRepository
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.deencompanion.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

data class QuranDetailUiState(
    val selectedTranslation: String = "en",
    val isWordByWordMode: Boolean = false,
    val selectedAyahIndex: Int = 0,
    val normalSurahState: UiState<List<AyahDetail>> = UiState.Loading,
    val wordByWordState: UiState<List<WordVerse>> = UiState.Loading,
    val audioAyahs: List<AudioAyahItem> = emptyList(),
    val isAudioPlaying: Boolean = false,
    val currentAudioAyahIndex: Int = -1,
    val audioProgress: Float = 0f,
    val currentDurationMs: Int = 0,
    val currentPositionMs: Int = 0
)

@HiltViewModel
class QuranDetailViewModel @Inject constructor(
    private val getSurahDetailUseCase: GetSurahDetailUseCase,
    private val getWordByWordUseCase: GetWordByWordUseCase,
    private val quranRepository: QuranRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val surahId: Int = checkNotNull(savedStateHandle["surahId"]) {
        "surahId is required for QuranDetailViewModel"
    }

    private val _uiState = MutableStateFlow(QuranDetailUiState())
    val uiState: StateFlow<QuranDetailUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    init {
        viewModelScope.launch {
            val defaultLang = settingsRepository.defaultTranslationLanguage.first()
            _uiState.update { it.copy(selectedTranslation = defaultLang) }
            loadSurahData()
            loadWordByWordData()
        }
    }
    fun loadSurahData() {
        viewModelScope.launch {
            _uiState.update { it.copy(normalSurahState = UiState.Loading) }

            getSurahDetailUseCase(surahId).fold(
                onSuccess = { ayahs -> _uiState.update { it.copy(normalSurahState = UiState.Success(ayahs)) } },
                onFailure = { error -> _uiState.update { it.copy(normalSurahState = UiState.Error(error.localizedMessage ?: "Failed to load translations")) } }
            )

            quranRepository.getSurahAudio(surahId).onSuccess { audioItems ->
                _uiState.update { it.copy(audioAyahs = audioItems) }
            }
        }
    }

    private fun loadWordByWordData() {
        viewModelScope.launch {
            _uiState.update { it.copy(wordByWordState = UiState.Loading) }
            val lang = _uiState.value.selectedTranslation
            getWordByWordUseCase(surahId, lang).fold(
                onSuccess = { verses -> _uiState.update { it.copy(wordByWordState = UiState.Success(verses)) } },
                onFailure = { error -> _uiState.update { it.copy(wordByWordState = UiState.Error(error.localizedMessage ?: "Failed to load word-by-word")) } }
            )
        }
    }

    fun setTranslation(lang: String) {
        _uiState.update { it.copy(selectedTranslation = lang) }
        loadWordByWordData()
    }

    fun toggleMode(isWordByWord: Boolean) {
        _uiState.update { it.copy(isWordByWordMode = isWordByWord) }
    }

    fun setSelectedAyahIndex(index: Int) {
        _uiState.update { it.copy(selectedAyahIndex = index) }
    }

    fun playAyahAudio(url: String, index: Int) {
        viewModelScope.launch {
            try {
                progressJob?.cancel()
                mediaPlayer?.release()
                _uiState.update { it.copy(currentAudioAyahIndex = index, isAudioPlaying = true, audioProgress = 0f, selectedAyahIndex = index) }

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { mp -> mp.start(); startProgressTracker() }
                    setOnCompletionListener { handlePlaybackCompletion() }
                    setOnErrorListener { _, _, _ -> _uiState.update { it.copy(isAudioPlaying = false) }; true }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAudioPlaying = false) }
            }
        }
    }

    private fun startProgressTracker() {
        progressJob = viewModelScope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val duration = mp.duration
                        val position = mp.currentPosition
                        val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
                        _uiState.update { it.copy(audioProgress = progress, currentDurationMs = duration, currentPositionMs = position) }
                    }
                }
                delay(200)
            }
        }
    }

    fun toggleAudioPlayPause() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _uiState.update { it.copy(isAudioPlaying = false) }
            } else {
                mp.start()
                _uiState.update { it.copy(isAudioPlaying = true) }
                startProgressTracker()
            }
        } ?: run {
            val state = _uiState.value
            val currentIndex = state.selectedAyahIndex
            if (state.audioAyahs.isNotEmpty() && currentIndex in state.audioAyahs.indices) {
                playAyahAudio(state.audioAyahs[currentIndex].audio, currentIndex)
            }
        }
    }

    fun seekTo(progress: Float) {
        mediaPlayer?.let { mp ->
            val duration = mp.duration
            if (duration > 0) {
                val position = (progress * duration).toInt()
                mp.seekTo(position)
                _uiState.update { it.copy(audioProgress = progress, currentPositionMs = position) }
            }
        }
    }

    private fun handlePlaybackCompletion() {
        progressJob?.cancel()
        val state = _uiState.value
        val nextIndex = state.currentAudioAyahIndex + 1
        if (nextIndex < state.audioAyahs.size) {
            playAyahAudio(state.audioAyahs[nextIndex].audio, nextIndex)
        } else {
            _uiState.update { it.copy(isAudioPlaying = false, currentAudioAyahIndex = -1, audioProgress = 0f, currentPositionMs = 0) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()
    }
}