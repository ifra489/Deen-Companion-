package com.deencompanion.app.presentation.ui.zakat



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.*
import com.deencompanion.app.domain.usecase.CalculateZakatUseCase
import com.deencompanion.app.domain.usecase.GetZakatRatesUseCase
import com.deencompanion.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZakatViewModel @Inject constructor(
    private val getZakatRatesUseCase: GetZakatRatesUseCase,
    private val calculateZakatUseCase: CalculateZakatUseCase
) : ViewModel() {

    private val _ratesState = MutableStateFlow<UiState<ZakatRates>>(UiState.Loading)
    val ratesState: StateFlow<UiState<ZakatRates>> = _ratesState.asStateFlow()

    private val _result = MutableStateFlow<ZakatResult?>(null)
    val result: StateFlow<ZakatResult?> = _result.asStateFlow()

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            _ratesState.value = UiState.Loading
            getZakatRatesUseCase().fold(
                onSuccess = { _ratesState.value = UiState.Success(it) },
                onFailure = { _ratesState.value = UiState.Error(it.message ?: "Failed to load rates") }
            )
        }
    }

    fun calculate(
        cashAndBank: Double,
        goldGrams: Double,
        silverGrams: Double,
        businessAssets: Double,
        liabilities: Double,
        nisabBasis: NisabBasis
    ) {
        val rates = (ratesState.value as? UiState.Success)?.data ?: return
        val input = ZakatInput(cashAndBank, goldGrams, silverGrams, businessAssets, liabilities, nisabBasis)
        _result.value = calculateZakatUseCase(input, rates)
    }

    fun clearResult() {
        _result.value = null
    }
}