//package com.deencompanion.app.presentation.ui.dua
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ContentCopy
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalClipboardManager
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.font.FontStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.deencompanion.app.domain.model.Dua
//import com.deencompanion.app.util.UiState
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DuaDetailScreen(
//    navController: NavController,
//    viewModel: DuaDetailViewModel = hiltViewModel()
//) {
//    val state by viewModel.duaState.collectAsState()
//    val context = LocalContext.current
//    val clipboardManager = LocalClipboardManager.current
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Dua Details",
//                        color = Color(0xFF212121),
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color(0xFF212121)
//                        )
//                    }
//                },
//                actions = {
//                    if (state is UiState.Success) {
//                        val dua = (state as UiState.Success<Dua>).data
//                        IconButton(onClick = {
//                            clipboardManager.setText(AnnotatedString(dua.arabic))
//                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.ContentCopy,
//                                contentDescription = "Copy Arabic text",
//                                tint = Color(0xFF212121)
//                            )
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFFF5F5F5)
//                )
//            )
//        },
//        containerColor = Color(0xFFF5F5F5)
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            when (state) {
//                is UiState.Loading -> {
//                    CircularProgressIndicator(
//                        color = Color(0xFF212121),
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                is UiState.Error -> {
//                    val errorMessage = (state as UiState.Error).message
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = errorMessage,
//                            color = Color.Red,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//                        Button(
//                            onClick = { navController.popBackStack() },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
//                        ) {
//                            Text("Go Back", color = Color.White)
//                        }
//                    }
//                }
//                is UiState.Empty -> {
//                    Text(
//                        text = "Dua not found",
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                is UiState.Success -> {
//                    val dua = (state as UiState.Success<Dua>).data
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .verticalScroll(rememberScrollState())
//                            .padding(16.dp)
//                    ) {
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(20.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = dua.arabic,
//                                    fontSize = 28.sp,
//                                    lineHeight = 42.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    textAlign = TextAlign.Center,
//                                    color = Color(0xFF212121),
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//
//                                Spacer(modifier = Modifier.height(16.dp))
//
//                                Text(
//                                    text = dua.transliteration,
//                                    fontSize = 16.sp,
//                                    lineHeight = 22.sp,
//                                    fontStyle = FontStyle.Italic,
//                                    textAlign = TextAlign.Center,
//                                    color = Color(0xFF212121).copy(alpha = 0.8f)
//                                )
//                            }
//                        }
//
//                        Card(
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                            ) {
//                                val preferredLanguage by viewModel.preferredLanguage.collectAsState()
//                                val orderedSections = remember(preferredLanguage) {
//                                    val all = listOf(
//                                        Triple("en", "ENGLISH", dua.english),
//                                        Triple("ur", "URDU", dua.urdu),
//                                        Triple("roman", "ROMAN URDU", dua.romanUrdu)
//                                    )
//                                    all.sortedByDescending { it.first == preferredLanguage }
//                                }
//
//                                orderedSections.forEachIndexed { index, (_, label, content) ->
//                                    TranslationSection(label = label, content = content)
//                                    if (index != orderedSections.lastIndex) {
//                                        HorizontalDivider(
//                                            modifier = Modifier.padding(vertical = 12.dp),
//                                            color = Color(0xFFF5F5F5)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        if (dua.reference.isNotBlank()) {
//                            Text(
//                                text = "Reference: ${dua.reference}",
//                                fontSize = 12.sp,
//                                color = Color.Gray,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 8.dp),
//                                textAlign = TextAlign.Start
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TranslationSection(label: String, content: String) {
//    if (content.isNotBlank()) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)
//        ) {
//            Text(
//                text = label,
//                fontSize = 11.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF212121).copy(alpha = 0.5f),
//                letterSpacing = 1.sp
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = content,
//                fontSize = 15.sp,
//                lineHeight = 22.sp,
//                color = Color(0xFF212121)
//            )
//        }
//    }
//}




package com.deencompanion.app.presentation.ui.dua

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deencompanion.app.domain.model.Dua
import com.deencompanion.app.presentation.ui.bookmarks.BookmarksViewModel
import com.deencompanion.app.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaDetailScreen(
    navController: NavController,
    viewModel: DuaDetailViewModel = hiltViewModel(),
    bookmarksViewModel: BookmarksViewModel = hiltViewModel()
) {
    val state by viewModel.duaState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dua Details",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (state is UiState.Success) {
                        val dua = (state as UiState.Success<Dua>).data
                        val isBookmarked by bookmarksViewModel
                            .isDuaBookmarked(dua.id)
                            .collectAsState(initial = false)

                        IconButton(onClick = {
                            bookmarksViewModel.toggleDua(
                                duaId = dua.id,
                                title = dua.transliteration.ifBlank { "Dua ${dua.id}" },
                                snippet = dua.arabic
                            )
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark this dua",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(dua.arabic))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Arabic text",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Error -> {
                    val errorMessage = (state as UiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Go Back", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
                is UiState.Empty -> {
                    Text(
                        text = "Dua not found",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    val dua = (state as UiState.Success<Dua>).data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dua.arabic,
                                    fontSize = 28.sp,
                                    lineHeight = 42.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = dua.transliteration,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val preferredLanguage by viewModel.preferredLanguage.collectAsState()
                                val orderedSections = remember(preferredLanguage) {
                                    val all = listOf(
                                        Triple("en", "ENGLISH", dua.english),
                                        Triple("ur", "URDU", dua.urdu),
                                        Triple("roman", "ROMAN URDU", dua.romanUrdu)
                                    )
                                    all.sortedByDescending { it.first == preferredLanguage }
                                }

                                orderedSections.forEachIndexed { index, (_, label, content) ->
                                    TranslationSection(label = label, content = content)
                                    if (index != orderedSections.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (dua.reference.isNotBlank()) {
                            Text(
                                text = "Reference: ${dua.reference}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TranslationSection(label: String, content: String) {
    if (content.isNotBlank()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}