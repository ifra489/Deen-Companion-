package com.deencompanion.app.presentation.ui.hadith



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Hadith
import com.deencompanion.app.domain.usecase.GetAllHadithsUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithListViewModel @Inject constructor(
    private val getAllHadithsUseCase: GetAllHadithsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _hadithListState = MutableStateFlow<UiState<List<Hadith>>>(UiState.Loading)
    val hadithListState: StateFlow<UiState<List<Hadith>>> = _hadithListState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _filteredHadiths = MutableStateFlow<List<Hadith>>(emptyList())
    val filteredHadiths: StateFlow<List<Hadith>> = _filteredHadiths.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private var allHadiths: List<Hadith> = emptyList()

    init {
        loadHadiths()
    }

    fun loadHadiths() {
        viewModelScope.launch {
            _hadithListState.value = UiState.Loading
            try {
                val hadiths = getAllHadithsUseCase(context)
                allHadiths = hadiths
                _categories.value = hadiths.flatMap { it.category }.distinct().sorted()
                _hadithListState.value = if (hadiths.isEmpty()) UiState.Empty else UiState.Success(hadiths)
                applyFilters()
            } catch (e: Exception) {
                _hadithListState.value = UiState.Error(e.message ?: "Failed to load hadiths")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
        applyFilters()
    }

    private fun applyFilters() {
        var result = allHadiths
        _selectedCategory.value?.let { cat ->
            result = result.filter { it.category.contains(cat) }
        }
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            result = result.filter {
                it.english.contains(query, ignoreCase = true) ||
                        it.narrator.contains(query, ignoreCase = true) ||
                        it.reference.contains(query, ignoreCase = true)
            }
        }
        _filteredHadiths.value = result
    }
}