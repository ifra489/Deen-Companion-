package com.deencompanion.app.presentation.ui.qibla



import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * LEARNING NOTE:
 * Qibla direction is calculated using the great-circle bearing formula from the
 * user's current location to the Kaaba's fixed coordinates (Mecca, Saudi Arabia).
 * The device's live compass heading comes from combining the accelerometer
 * (gravity/tilt) and magnetometer (magnetic north) readings via
 * SensorManager.getRotationMatrix + getOrientation — the standard Android
 * approach for a tilt-compensated compass.
 */
data class QiblaUiState(
    val hasSensors: Boolean = true,
    val hasLocation: Boolean = false,
    val isLoadingLocation: Boolean = true,
    val deviceHeading: Float = 0f,      // 0-360 degrees from magnetic north
    val qiblaBearing: Float? = null,    // 0-360 degrees from true north to the Kaaba
    val locationError: String? = null,
    val isCalibrating: Boolean = false
)

@HiltViewModel
class QiblaViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener {

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var smoothedHeading: Float? = null

    companion object {
        private const val KAABA_LATITUDE = 21.4225
        private const val KAABA_LONGITUDE = 39.8262
    }

    init {
        if (accelerometer == null || magnetometer == null) {
            _uiState.update { it.copy(hasSensors = false) }
        }
        loadLocationAndQibla()
    }

    fun startListening() {
        if (!_uiState.value.hasSensors) return
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun loadLocationAndQibla() {
        _uiState.update { it.copy(isLoadingLocation = true, locationError = null) }
        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()
                val location: Location? = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                    .await()

                if (location != null) {
                    val bearing = calculateQiblaBearing(location.latitude, location.longitude)
                    _uiState.update {
                        it.copy(
                            hasLocation = true,
                            qiblaBearing = bearing,
                            locationError = null,
                            isLoadingLocation = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            hasLocation = false,
                            isLoadingLocation = false,
                            locationError = "Couldn't get your location. Make sure location services are turned on."
                        )
                    }
                }
            } catch (e: SecurityException) {
                _uiState.update {
                    it.copy(
                        hasLocation = false,
                        isLoadingLocation = false,
                        locationError = "Location permission is needed to find the Qibla direction."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        hasLocation = false,
                        isLoadingLocation = false,
                        locationError = "Couldn't get your location. Please try again."
                    )
                }
            }
        }
    }

    /**
     * Great-circle initial bearing from (lat, lon) to the Kaaba, in degrees
     * clockwise from true north (0-360).
     */
    private fun calculateQiblaBearing(lat: Double, lon: Double): Float {
        val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
        val kaabaLonRad = Math.toRadians(KAABA_LONGITUDE)
        val userLatRad = Math.toRadians(lat)
        val userLonRad = Math.toRadians(lon)

        val deltaLon = kaabaLonRad - userLonRad
        val y = sin(deltaLon) * cos(kaabaLatRad)
        val x = cos(userLatRad) * sin(kaabaLatRad) - sin(userLatRad) * cos(kaabaLatRad) * cos(deltaLon)
        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        bearing = (bearing + 360f) % 360f
        return bearing
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values.clone()
        }

        val gravityValues = gravity
        val geomagneticValues = geomagnetic
        if (gravityValues != null && geomagneticValues != null) {
            val rotationMatrix = FloatArray(9)
            val inclinationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix, inclinationMatrix, gravityValues, geomagneticValues
            )
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360f) % 360f

                // Low-pass angular smoothing so the needle doesn't jitter.
                val previous = smoothedHeading
                val smoothed = if (previous == null) azimuth else lerpAngle(previous, azimuth, 0.15f)
                smoothedHeading = smoothed

                _uiState.update { it.copy(deviceHeading = smoothed) }
            }
        }
    }

    private fun lerpAngle(from: Float, to: Float, t: Float): Float {
        val diff = (to - from + 540f) % 360f - 180f
        return (from + diff * t + 360f) % 360f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _uiState.update { it.copy(isCalibrating = accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW) }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}