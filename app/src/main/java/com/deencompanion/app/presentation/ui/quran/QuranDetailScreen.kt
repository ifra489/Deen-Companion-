package com.deencompanion.app.presentation.ui.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranDetailScreen(
    surahId: Int,
    viewModel: QuranDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val bookmarksViewModel: BookmarksViewModel = hiltViewModel()

    val currentSurah = remember(surahId) {
        Surah.ALL_SURAHS.find { it.number == surahId }
    }

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
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentSurah?.nameEnglish ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        SegmentedButton(
                            selected = !uiState.isWordByWordMode,
                            onClick = { viewModel.toggleMode(false) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = Color.White,
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Normal", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        }
                        SegmentedButton(
                            selected = uiState.isWordByWordMode,
                            onClick = { viewModel.toggleMode(true) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = Color.White,
                                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("W-B-W", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (uiState.audioAyahs.isNotEmpty()) {
                AudioPlayerBar(
                    uiState = uiState,
                    onPlayPauseClick = { viewModel.toggleAudioPlayPause() },
                    onSeek = { viewModel.seekTo(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val totalAyahs = currentSurah?.versesCount ?: 0
            if (totalAyahs > 0) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
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
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = null,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("en" to "English", "ur" to "Urdu", "hi" to "Hindi")
                languages.forEach { (code, label) ->
                    val isSelected = uiState.selectedTranslation == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTranslation(code) },
                        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!uiState.isWordByWordMode) {
                when (val state = uiState.normalSurahState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadSurahData() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                                    selectedTranslation = uiState.selectedTranslation,
                                    isHighlighted = isHighlighted,
                                    isAyahPlaying = isAyahPlaying,
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
                    else -> {}
                }
            } else {
                when (val state = uiState.wordByWordState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadWordByWordData() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(state.data) { index, verse ->
                                val isHighlighted = index == uiState.selectedAyahIndex
                                WordByWordCard(
                                    verse = verse,
                                    isHighlighted = isHighlighted
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun NormalAyahCard(
    ayah: AyahDetail,
    surahId: Int,
    selectedTranslation: String,
    isHighlighted: Boolean,
    isAyahPlaying: Boolean,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isHighlighted) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                ) {
                    Text(
                        text = ayah.numberInSurah.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBookmarkClick) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onPlayClick) {
                        Icon(
                            imageVector = if (isAyahPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val bismillah1 = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
            val bismillah2 = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
            val startsWithBismillah1 = ayah.arabicText.startsWith(bismillah1)
            val startsWithBismillah2 = ayah.arabicText.startsWith(bismillah2)
            val foundBismillah = if (startsWithBismillah1) bismillah1 else if (startsWithBismillah2) bismillah2 else null
            val isSurahFatihaAyah1 = surahId == 1 && ayah.numberInSurah == 1
            val shouldExtractBismillah = ayah.numberInSurah == 1 && foundBismillah != null && surahId != 1 && surahId != 9

            if (isSurahFatihaAyah1 || shouldExtractBismillah) {
                Text(
                    text = foundBismillah ?: ayah.arabicText,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        textDirection = TextDirection.Rtl
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            val remainingText = if (shouldExtractBismillah && foundBismillah != null) {
                val text = ayah.arabicText.substringAfter(foundBismillah).trim()
                if (text.isEmpty()) null else text
            } else if (isSurahFatihaAyah1) {
                null
            } else {
                ayah.arabicText
            }

            remainingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 24.sp,
                        textAlign = if (shouldExtractBismillah) TextAlign.Center else TextAlign.End,
                        textDirection = TextDirection.Rtl
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val translationText = ayah.translations[selectedTranslation] ?: "Translation not available."
            Text(
                text = translationText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    isHighlighted: Boolean
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isHighlighted) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Ayah ${verse.verseNumber}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = word.arabicText,
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = word.translation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(max = 100.dp)
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
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraLarge)
                ) {
                    Icon(
                        imageVector = if (uiState.isAudioPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.currentAudioAyahIndex != -1) "Ayah ${uiState.currentAudioAyahIndex + 1}" else "Select Ayah",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sheikh Mishary Al-Alafasy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${formatTime(uiState.currentPositionMs)} / ${formatTime(uiState.currentDurationMs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = uiState.audioProgress,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    thumbColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
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
