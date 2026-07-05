package com.deencompanion.app.domain.model

/**
 * LEARNING NOTE:
 * This is a clean Kotlin domain model for the User.
 * It is completely independent of Firebase or database frameworks, adhering to Clean Architecture principles.
 * The Presentation layer maps FirebaseUser objects to this model to avoid exposing raw SDK models.
 */
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val isGuest: Boolean,
    val createdAt: Long
)
