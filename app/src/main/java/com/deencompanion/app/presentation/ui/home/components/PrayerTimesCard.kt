package com.deencompanion.app.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deencompanion.app.domain.model.PrayerTimes
import com.deencompanion.app.util.UiState
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PrayerTimesCard(
    state: UiState<PrayerTimes>,
    countdown: String,
    onRetry: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is UiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Calculating Prayer Times...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = "Connection Error",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Retry")
                    }
                }
                is UiState.Success -> {
                    val prayerTimes = state.data
                    val nextPrayerName = getNextPrayerName(prayerTimes)

                    Text(
                        text = "Next: $nextPrayerName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = countdown,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val prayersList = listOf(
                            "Fajr" to prayerTimes.fajr,
                            "Dhuhr" to prayerTimes.dhuhr,
                            "Asr" to prayerTimes.asr,
                            "Maghrib" to prayerTimes.maghrib,
                            "Isha" to prayerTimes.isha
                        )

                        prayersList.forEach { (name, time) ->
                            val isNext = name == nextPrayerName
                            val cleanTime = time.substringBefore(" ").trim()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cleanTime,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

private fun getNextPrayerName(prayerTimes: PrayerTimes): String {
    val now = LocalTime.now()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    val prayers = listOf(
        "Fajr" to prayerTimes.fajr,
        "Dhuhr" to prayerTimes.dhuhr,
        "Asr" to prayerTimes.asr,
        "Maghrib" to prayerTimes.maghrib,
        "Isha" to prayerTimes.isha
    )

    val parsedTimes = prayers.mapNotNull { (name, timeString) ->
        try {
            val cleaned = timeString.substringBefore(" ").trim()
            name to LocalTime.parse(cleaned, timeFormatter)
        } catch (e: Exception) {
            null
        }
    }

    if (parsedTimes.isEmpty()) return "Fajr"

    val next = parsedTimes.filter { it.second.isAfter(now) }.minByOrNull { it.second }
    return next?.first ?: "Fajr"
}
