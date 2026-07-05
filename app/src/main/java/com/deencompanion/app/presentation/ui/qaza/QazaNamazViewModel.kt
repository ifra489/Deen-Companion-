package com.deencompanion.app.presentation.ui.qaza



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.QazaPrayer
import com.deencompanion.app.domain.model.QazaSettings
import com.deencompanion.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QazaNamazViewModel @Inject constructor(
    private val getQazaSettingsUseCase: GetQazaSettingsUseCase,
    private val getQazaPrayersUseCase: GetQazaPrayersUseCase,
    private val calculateQazaNamazUseCase: CalculateQazaNamazUseCase,
    private val markQazaCompletedUseCase: MarkQazaCompletedUseCase,
    private val resetQazaCalculationUseCase: ResetQazaCalculationUseCase
) : ViewModel() {

    val settings: StateFlow<QazaSettings?> = getQazaSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val prayers: StateFlow<List<QazaPrayer>> = getQazaPrayersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun calculate(currentAge: Int, obligationAge: Int, ageStartedPraying: Int) {
        viewModelScope.launch {
            calculateQazaNamazUseCase(currentAge, obligationAge, ageStartedPraying)
        }
    }

    fun markCompleted(prayerType: String, amount: Int) {
        viewModelScope.launch { markQazaCompletedUseCase(prayerType, amount) }
    }

    fun resetCalculation() {
        viewModelScope.launch { resetQazaCalculationUseCase() }
    }
}