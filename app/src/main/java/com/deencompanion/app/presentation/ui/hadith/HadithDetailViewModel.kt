package com.deencompanion.app.presentation.ui.hadith



import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Hadith
import com.deencompanion.app.domain.usecase.GetHadithByIdUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithDetailViewModel @Inject constructor(
    private val getHadithByIdUseCase: GetHadithByIdUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hadithId: Int = checkNotNull(savedStateHandle["hadithId"])

    private val _hadithState = MutableStateFlow<UiState<Hadith>>(UiState.Loading)
    val hadithState: StateFlow<UiState<Hadith>> = _hadithState.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        loadHadith()
    }

    fun loadHadith() {
        viewModelScope.launch {
            _hadithState.value = UiState.Loading
            val hadith = getHadithByIdUseCase(context, hadithId)
            _hadithState.value = if (hadith != null) UiState.Success(hadith) else UiState.Error("Hadith not found")
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }
}