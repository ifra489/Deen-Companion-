package com.deencompanion.app.presentation.ui.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.PrayerRecord
import com.deencompanion.app.domain.usecase.prayer.GetPrayerHistoryUseCase
import com.deencompanion.app.domain.usecase.prayer.GetTodayPrayerStatusUseCase
import com.deencompanion.app.domain.usecase.prayer.MarkPrayerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val markPrayerStatusUseCase: MarkPrayerStatusUseCase,
    private val getTodayPrayerStatusUseCase: GetTodayPrayerStatusUseCase,
    private val getPrayerHistoryUseCase: GetPrayerHistoryUseCase,
    private val getCurrentStreakUseCase: com.deencompanion.app.domain.usecase.prayer.GetCurrentStreakUseCase,
    private val getWeeklyProgressUseCase: com.deencompanion.app.domain.usecase.prayer.GetWeeklyProgressUseCase
) : ViewModel() {

    private val _todayPrayerStatus = MutableStateFlow<List<PrayerRecord>>(emptyList())
    val todayPrayerStatus: StateFlow<List<PrayerRecord>> = _todayPrayerStatus.asStateFlow()

    private val _prayerHistory = MutableStateFlow<List<PrayerRecord>>(emptyList())
    val prayerHistory: StateFlow<List<PrayerRecord>> = _prayerHistory.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _weeklyProgress = MutableStateFlow<List<com.deencompanion.app.domain.model.DailyProgress>>(emptyList())
    val weeklyProgress: StateFlow<List<com.deencompanion.app.domain.model.DailyProgress>> = _weeklyProgress.asStateFlow()

    init {
        val todayStr = LocalDate.now().toString()
        viewModelScope.launch {
            getTodayPrayerStatusUseCase(todayStr).collectLatest { list ->
                _todayPrayerStatus.value = list
            }
        }
        loadStreak()
        loadWeeklyProgress()
    }

    fun loadStreak() {
        viewModelScope.launch {
            _currentStreak.value = getCurrentStreakUseCase()
        }
    }

    fun loadWeeklyProgress() {
        viewModelScope.launch {
            _weeklyProgress.value = getWeeklyProgressUseCase()
        }
    }

    fun togglePrayerStatus(prayerName: String, isPrayed: Boolean) {
        val todayStr = LocalDate.now().toString()
        viewModelScope.launch(Dispatchers.IO) {
            markPrayerStatusUseCase(todayStr, prayerName, isPrayed)
            loadStreak()
            loadWeeklyProgress()
        }
    }

    fun loadHistory(daysBack: Int = 7) {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(daysBack.toLong())
        viewModelScope.launch {
            getPrayerHistoryUseCase(startDate.toString(), endDate.toString()).collectLatest { list ->
                _prayerHistory.value = list
            }
        }
    }
}
