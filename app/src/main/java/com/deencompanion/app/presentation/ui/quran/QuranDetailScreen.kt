package com.deencompanion.app.presentation.ui.quran


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deencompanion.app.domain.model.AyahDetail
import com.deencompanion.app.domain.model.Surah
import com.deencompanion.app.domain.model.WordVerse
import com.deencompanion.app.presentation.ui.bookmarks.BookmarksViewModel
import com.deencompanion.app.util.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranDetailScreen(
    surahId: Int,
    viewModel: QuranDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val bookmarksViewModel: BookmarksViewModel = hiltViewModel()

    val appBackground = MaterialTheme.colorScheme.background
    val appGreenAccent = MaterialTheme.colorScheme.primary
    val appTextPrimary = MaterialTheme.colorScheme.onBackground

    val currentSurah = remember(surahId) {
        Surah.ALL_SURAHS.find { it.number == surahId }
    }

    // Sync selected tab jumps with the list scrolling
    LaunchedEffect(uiState.selectedAyahIndex) {
        if (uiState.selectedAyahIndex >= 0) {
            listState.animateScrollToItem(uiState.selectedAyahIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentSurah?.nameTransliteration ?: "Surah Details",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = currentSurah?.nameEnglish ?: "",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    // Segmented Button or Toggle Row to switch modes
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        SegmentedButton(
                            selected = !uiState.isWordByWordMode,
                            onClick = { viewModel.toggleMode(false) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.surface,
                                activeContentColor = appGreenAccent,
                                inactiveContainerColor = Color.Transparent,
                                inactiveContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Normal", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = uiState.isWordByWordMode,
                            onClick = { viewModel.toggleMode(true) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.surface,
                                activeContentColor = appGreenAccent,
                                inactiveContainerColor = Color.Transparent,
                                inactiveContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("W-B-W", fontSize = 11.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appGreenAccent)
            )
        },
        bottomBar = {
            // Persistent Audio Recitation Player Sheet
            if (uiState.audioAyahs.isNotEmpty()) {
                AudioPlayerBar(
                    uiState = uiState,
                    appGreenAccent = appGreenAccent,
                    onPlayPauseClick = { viewModel.toggleAudioPlayPause() },
                    onSeek = { viewModel.seekTo(it) }
                )
            }
        },
        containerColor = appBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 1. Horizontal Scrollable Pager tabs Row ("Ayat 1", "Ayat 2", ...)
            val totalAyahs = currentSurah?.versesCount ?: 0
            if (totalAyahs > 0) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(totalAyahs) { i ->
                        val isSelected = uiState.selectedAyahIndex == i
                        AssistChip(
                            onClick = { viewModel.setSelectedAyahIndex(i) },
                            label = { Text("Ayat ${i + 1}") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) appGreenAccent else appBackground,
                                labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else appTextPrimary
                            ),
                            border = null
                        )
                    }
                }
            }

            // 2. Language Translation Filter Chips (Only shown in Normal mode)
            // 2. Language Translation Filter Chips (Normal aur Word-by-Word dono modes mein)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("en" to "English", "ur" to "Urdu", "hi" to "Hindi")
                languages.forEach { (code, label) ->
                    val isSelected = uiState.selectedTranslation == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTranslation(code) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = appGreenAccent,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Central Ayah Readings List
            if (!uiState.isWordByWordMode) {
                // NORMAL MODE SCREEN
                when (val state = uiState.normalSurahState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = appGreenAccent)
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                    is UiState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No ayahs loaded.")
                        }
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(state.data) { index, ayah ->
                                val isHighlighted = index == uiState.selectedAyahIndex
                                val isAyahPlaying = uiState.isAudioPlaying && uiState.currentAudioAyahIndex == index
                                val isBookmarked by bookmarksViewModel
                                    .isAyahBookmarked(surahId, ayah.numberInSurah)
                                    .collectAsState(initial = false)
                                NormalAyahCard(
                                    ayah = ayah,
                                    surahId = surahId,
                                    index = index,
                                    selectedTranslation = uiState.selectedTranslation,
                                    isHighlighted = isHighlighted,
                                    isAyahPlaying = isAyahPlaying,
                                    appGreenAccent = appGreenAccent,
                                    appTextPrimary = appTextPrimary,
                                    isBookmarked = isBookmarked,
                                    onBookmarkClick = {
                                        bookmarksViewModel.toggleAyah(
                                            surahId = surahId,
                                            surahName = currentSurah?.nameTransliteration ?: "Surah $surahId",
                                            ayahNumber = ayah.numberInSurah,
                                            arabicSnippet = ayah.arabicText
                                        )
                                    },
                                    onPlayClick = {
                                        if (isAyahPlaying) {
                                            viewModel.toggleAudioPlayPause()
                                        } else {
                                            if (index < uiState.audioAyahs.size) {
                                                viewModel.playAyahAudio(uiState.audioAyahs[index].audio, index)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // WORD-BY-WORD MODE SCREEN
                when (val state = uiState.wordByWordState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = appGreenAccent)
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        }
                    }
                    is UiState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No WBW data found.")
                        }
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(state.data) { index, verse ->
                                val isHighlighted = index == uiState.selectedAyahIndex
                                WordByWordCard(
                                    verse = verse,
                                    isHighlighted = isHighlighted,
                                    appGreenAccent = appGreenAccent,
                                    appTextPrimary = appTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NormalAyahCard(
    ayah: AyahDetail,
    surahId: Int,
    index: Int,
    selectedTranslation: String,
    isHighlighted: Boolean,
    isAyahPlaying: Boolean,
    appGreenAccent: Color,
    appTextPrimary: Color,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) appGreenAccent.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isHighlighted) BorderStroke(1.dp, appGreenAccent.copy(alpha = 0.3f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Ayah Number Badge + Bookmark + Quick Play button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .background(appGreenAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = ayah.numberInSurah.toString(),
                        fontWeight = FontWeight.Bold,
                        color = appGreenAccent,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBookmarkClick) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark this ayah",
                            tint = appGreenAccent
                        )
                    }
                    IconButton(onClick = onPlayClick) {
                        Icon(
                            imageVector = if (isAyahPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isAyahPlaying) "Pause" else "Listen",
                            tint = appGreenAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Text
            val bismillah = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
            val displayArabicText = if (ayah.numberInSurah == 1 && ayah.arabicText.startsWith(bismillah) && surahId != 1 && surahId != 9) {
                ayah.arabicText.replaceFirst(bismillah, "$bismillah\n")
            } else {
                ayah.arabicText
            }

            Text(
                text = displayArabicText,
                fontWeight = FontWeight.Bold,
                color = appTextPrimary,
                fontSize = 24.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.End,
                style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Selected Translation
            val translationText = ayah.translations[selectedTranslation] ?: "Translation not available."
            Text(
                text = translationText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordByWordCard(
    verse: WordVerse,
    isHighlighted: Boolean,
    appGreenAccent: Color,
    appTextPrimary: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) appGreenAccent.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isHighlighted) BorderStroke(1.dp, appGreenAccent.copy(alpha = 0.3f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ayah Reference Index
            Text(
                text = "Ayah ${verse.verseNumber} (Key: ${verse.verseKey})",
                fontWeight = FontWeight.SemiBold,
                color = appGreenAccent,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // FlowRow wraps words beautifully from Right-To-Left direction
            CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    verse.words.forEach { word ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = word.arabicText,
                                fontWeight = FontWeight.Bold,
                                color = appTextPrimary,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = word.translation,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(max = 80.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioPlayerBar(
    uiState: QuranDetailUiState,
    appGreenAccent: Color,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Icon Button
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(appGreenAccent, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = if (uiState.isAudioPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Playing Verse Title and Status Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.currentAudioAyahIndex != -1) {
                            "Reciting Ayah ${uiState.currentAudioAyahIndex + 1}"
                        } else {
                            "Select an Ayah to Play"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sheikh Mishary Al-Alafasy",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Time Indicator Label
                Text(
                    text = "${formatTime(uiState.currentPositionMs)} / ${formatTime(uiState.currentDurationMs)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Smooth Audio Progress Slider
            Slider(
                value = uiState.audioProgress,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    activeTrackColor = appGreenAccent,
                    thumbColor = appGreenAccent,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun formatTime(millis: Int): String {
    val totalSecs = millis / 1000
    val minutes = totalSecs / 60
    val seconds = totalSecs % 60
    return String.format("%02d:%02d", minutes, seconds)
}
