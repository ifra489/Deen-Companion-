package com.deencompanion.app.util

/**
 * LEARNING NOTE:
 * This sealed class represents all the possible states of a UI screen (Loading, Success, Error, or Empty).
 * It acts as a standardized wrapper for data fetched from repositories or networks before presenting it in the UI.
 * ViewModels will update their state flows with this type, and Compose UI screens will observe and render different UI components accordingly.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
