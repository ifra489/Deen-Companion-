package com.deencompanion.app.presentation.ui.home
import com.deencompanion.app.util.notification.AdhanScheduler
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.domain.model.Ayah
import com.deencompanion.app.domain.model.DailyDua
import com.deencompanion.app.domain.model.DailyHadith
import com.deencompanion.app.domain.model.HijriDate
import com.deencompanion.app.domain.model.PrayerTimes
import com.deencompanion.app.domain.usecase.home.GetDailyAyahUseCase
import com.deencompanion.app.domain.usecase.home.GetDailyDuaUseCase
import com.deencompanion.app.domain.usecase.home.GetDailyHadithUseCase
import com.deencompanion.app.domain.usecase.home.GetHijriDateUseCase
import com.deencompanion.app.domain.usecase.home.GetPrayerTimesUseCase
import com.deencompanion.app.util.UiState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val getHijriDateUseCase: GetHijriDateUseCase,
    private val getDailyAyahUseCase: GetDailyAyahUseCase,
    private val getDailyHadithUseCase: GetDailyHadithUseCase,
    private val getDailyDuaUseCase: GetDailyDuaUseCase,
    private val adhanScheduler: AdhanScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _prayerTimesState = MutableStateFlow<UiState<PrayerTimes>>(UiState.Loading)
    val prayerTimesState: StateFlow<UiState<PrayerTimes>> = _prayerTimesState.asStateFlow()

    private val _hijriDateState = MutableStateFlow<UiState<HijriDate>>(UiState.Loading)
    val hijriDateState: StateFlow<UiState<HijriDate>> = _hijriDateState.asStateFlow()

    private val _dailyAyahState = MutableStateFlow<UiState<Ayah>>(UiState.Loading)
    val dailyAyahState: StateFlow<UiState<Ayah>> = _dailyAyahState.asStateFlow()

    private val _dailyHadith = MutableStateFlow<DailyHadith?>(null)
    val dailyHadith: StateFlow<DailyHadith?> = _dailyHadith.asStateFlow()

    private val _dailyDua = MutableStateFlow<DailyDua?>(null)
    val dailyDua: StateFlow<DailyDua?> = _dailyDua.asStateFlow()

    private val _countdownTimer = MutableStateFlow("00h 00m 00s")
    val countdownTimer: StateFlow<String> = _countdownTimer.asStateFlow()

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        loadPrayerTimesByLocation()
        loadHijriDate()
        loadDailyAyah()
        loadDailyHadith()
        loadDailyDua()
        loadUserName()
        startCountdownTimer()
    }

    /**
     * Automatically detect location and load prayer times.
     * Falls back to Rawalpindi if location unavailable.
     */
    fun loadPrayerTimesByLocation() {
        _prayerTimesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()
                val location: Location? = fusedLocationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cancellationToken.token
                    ).await()

                if (location != null) {
                    loadPrayerTimes(location.latitude, location.longitude)
                } else {
                    // Fallback to Rawalpindi coordinates if location is null
                    loadPrayerTimes(33.5973, 73.0479)
                }
            } catch (e: SecurityException) {
                // Location permission not granted, fallback to Rawalpindi
                loadPrayerTimes(33.5973, 73.0479)
            } catch (e: Exception) {
                loadPrayerTimes(33.5973, 73.0479)
            }
        }
    }

    /**
     * Load prayer times for specific coordinates.
     */
    fun loadPrayerTimes(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getPrayerTimesUseCase(latitude, longitude)
                _prayerTimesState.value = result
                if (result is UiState.Success) {
                    adhanScheduler.scheduleAllPrayerAlarms(result.data)
                }
            } catch (e: Exception) {
                _prayerTimesState.value =
                    UiState.Error(e.message ?: "Failed to load prayer times")
            }
        }
    }

    private fun loadHijriDate() {
        _hijriDateState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _hijriDateState.value = getHijriDateUseCase()
            } catch (e: Exception) {
                _hijriDateState.value =
                    UiState.Error(e.message ?: "Failed to load Hijri date")
            }
        }
    }

    private fun loadDailyAyah() {
        _dailyAyahState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dailyAyahState.value = getDailyAyahUseCase()
            } catch (e: Exception) {
                _dailyAyahState.value =
                    UiState.Error(e.message ?: "Failed to load daily Ayah")
            }
        }
    }

    fun refreshAyah() {
        loadDailyAyah()
    }

    private fun loadDailyHadith() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dailyHadith.value = getDailyHadithUseCase(context)
            } catch (e: Exception) {
                _dailyHadith.value = null
            }
        }
    }

    private fun loadDailyDua() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dailyDua.value = getDailyDuaUseCase(context)
            } catch (e: Exception) {
                _dailyDua.value = null
            }
        }
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            _userName.value = when {
                currentUser == null -> "Guest"
                !currentUser.displayName.isNullOrBlank() -> currentUser.displayName!!
                !currentUser.email.isNullOrBlank() ->
                    currentUser.email!!.substringBefore("@")
                else -> "Guest"
            }
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                val state = _prayerTimesState.value
                if (state is UiState.Success) {
                    val nextTime = getNextPrayerTime(state.data)
                    if (nextTime != null) {
                        val now = LocalTime.now()
                        val duration = if (nextTime.isAfter(now)) {
                            Duration.between(now, nextTime)
                        } else {
                            Duration.between(now, nextTime).plusDays(1)
                        }
                        val hours = duration.toHours()
                        val minutes = duration.toMinutes() % 60
                        val seconds = duration.seconds % 60
                        _countdownTimer.value = String.format(
                            Locale.US,
                            "%02dh %02dm %02ds",
                            hours, minutes, seconds
                        )
                    } else {
                        _countdownTimer.value = "00h 00m 00s"
                    }
                } else {
                    _countdownTimer.value = "00h 00m 00s"
                }
                delay(1000L)
            }
        }
    }

    private fun getNextPrayerTime(prayerTimes: PrayerTimes): LocalTime? {
        val now = LocalTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)
        val prayers = listOf(
            prayerTimes.fajr,
            prayerTimes.dhuhr,
            prayerTimes.asr,
            prayerTimes.maghrib,
            prayerTimes.isha
        )
        val parsedTimes = prayers.mapNotNull { timeString ->
            try {
                LocalTime.parse(timeString.substringBefore(" ").trim(), timeFormatter)
            } catch (e: Exception) {
                null
            }
        }
        if (parsedTimes.isEmpty()) return null
        return parsedTimes.filter { it.isAfter(now) }.minOrNull()
            ?: parsedTimes.minOrNull()
    }
}