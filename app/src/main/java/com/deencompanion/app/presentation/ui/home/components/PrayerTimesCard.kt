package com.deencompanion.app.presentation.ui.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4527A0),
                            Color(0xFF6A1B9A)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is UiState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading Prayer Times...",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }

                }
                is UiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Could not load",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Text(text = "Retry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is UiState.Success -> {
                    val prayerTimes = state.data
                    val nextPrayerName = getNextPrayerName(prayerTimes)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Next Prayer",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nextPrayerName,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = countdown,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
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
                                val textColor = if (isNext) Color(0xFFF9A825) else Color.White
                                val labelColor = if (isNext) Color(0xFFF9A825).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.7f)
                                val cleanTime = time.substringBefore(" ").trim()

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = name,
                                        color = labelColor,
                                        fontSize = 12.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cleanTime,
                                        color = textColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Empty or other states
                    Text(
                        text = "No prayer times available",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
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
