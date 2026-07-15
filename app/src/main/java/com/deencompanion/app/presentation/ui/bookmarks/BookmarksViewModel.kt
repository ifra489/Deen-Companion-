package com.deencompanion.app.presentation.ui.bookmarks



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deencompanion.app.data.repository.BookmarkRepositoryImpl
import com.deencompanion.app.domain.model.Bookmark
import com.deencompanion.app.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isAyahBookmarked(surahId: Int, ayahNumber: Int): Flow<Boolean> =
        bookmarkRepository.isBookmarked(BookmarkRepositoryImpl.ayahBookmarkId(surahId, ayahNumber))

    fun isDuaBookmarked(duaId: Int): Flow<Boolean> =
        bookmarkRepository.isBookmarked(BookmarkRepositoryImpl.duaBookmarkId(duaId))

    fun toggleAyah(surahId: Int, surahName: String, ayahNumber: Int, arabicSnippet: String) {
        viewModelScope.launch {
            bookmarkRepository.toggleAyahBookmark(surahId, surahName, ayahNumber, arabicSnippet)
        }
    }

    fun toggleDua(duaId: Int, title: String, snippet: String) {
        viewModelScope.launch {
            bookmarkRepository.toggleDuaBookmark(duaId, title, snippet)
        }
    }

    fun remove(id: String) {
        viewModelScope.launch { bookmarkRepository.removeBookmark(id) }
    }
}