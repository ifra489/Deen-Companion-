package com.deencompanion.app.presentation.ui.quran


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deencompanion.app.domain.model.Surah
import com.deencompanion.app.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranListScreen(
    viewModel: QuranListViewModel,
    onSurahClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val surahState by viewModel.surahListState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val appBackground = MaterialTheme.colorScheme.background
    val appGreenAccent = MaterialTheme.colorScheme.primary
    val appTextPrimary = MaterialTheme.colorScheme.onBackground

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Holy Quran",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appGreenAccent
                )
            )
        },
        containerColor = appBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search Surah...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = appGreenAccent) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appGreenAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Content matching State
            when (val state = surahState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = appGreenAccent)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.loadSurahs() },
                                colors = ButtonDefaults.buttonColors(containerColor = appGreenAccent)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Surahs available.", color = appTextPrimary)
                    }
                }
                is UiState.Success -> {
                    val filteredSurahs = state.data.filter {
                        it.nameTransliteration.contains(searchQuery, ignoreCase = true) ||
                                it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                                it.number.toString() == searchQuery
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSurahs, key = { it.number }) { surah ->
                             SurahCardItem(
                                surah = surah,
                                appGreenAccent = appGreenAccent,
                                appTextPrimary = appTextPrimary,
                                onSurahClick = onSurahClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahCardItem(
    surah: Surah,
    appGreenAccent: Color,
    appTextPrimary: Color,
    onSurahClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onSurahClick(surah.number) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number inside circular badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(appGreenAccent.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = surah.number.toString(),
                    fontWeight = FontWeight.Bold,
                    color = appGreenAccent,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Surah English Name and Verse Counts
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.nameTransliteration,
                    fontWeight = FontWeight.Bold,
                    color = appTextPrimary,
                    fontSize = 16.sp
                )
                Text(
                    text = "${surah.nameEnglish} • ${surah.revelationType} • ${surah.versesCount} Ayahs",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            // Arabic Surah Name on the right
            Text(
                text = surah.nameArabic,
                fontWeight = FontWeight.Bold,
                color = appGreenAccent,
                fontSize = 20.sp,
                textAlign = TextAlign.End
            )
        }
    }
}