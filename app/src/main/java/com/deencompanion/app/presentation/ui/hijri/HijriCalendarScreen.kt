package com.deencompanion.app.presentation.ui.hijri



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.domain.model.HijriCalendarDay
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriCalendarScreen(
    navController: NavController,
    viewModel: HijriCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val gregorianFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hijri Calendar", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.monthNameEn} ${state.hijriYear} AH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(text = state.monthNameAr, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                IconButton(onClick = { viewModel.goToNextMonth() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday header row
            Row(modifier = Modifier.fillMaxWidth()) {
                dayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            Column(modifier = Modifier.fillMaxWidth()) {
                state.days.chunked(7).forEach { weekDays ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekDays.forEach { day ->
                            Box(modifier = Modifier.weight(1f)) {
                                HijriDayCell(day = day, formatter = gregorianFormatter)
                            }
                        }
                        if (weekDays.size < 7) {
                            repeat(7 - weekDays.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Important Islamic Dates (${state.hijriYear} AH)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            state.events.forEach { event ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(event.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text(event.hijriDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                        Text(
                            text = event.gregorianDate.format(gregorianFormatter),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Note: Hijri dates are astronomically calculated and may differ by a day from local moon sighting announcements.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HijriDayCell(day: HijriCalendarDay?, formatter: DateTimeFormatter) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${day.hijriDay}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (day.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${day.gregorianDate.dayOfMonth}",
                    fontSize = 9.sp,
                    color = if (day.isToday) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}