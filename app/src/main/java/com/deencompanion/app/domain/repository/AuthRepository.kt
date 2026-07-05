package com.deencompanion.app.domain.repository

import com.google.firebase.auth.FirebaseUser

/**
 * LEARNING NOTE:
 * This interface defines the contract for authentication operations.
 * It is defined in the Domain layer, meaning it does not contain details of the actual implementation.
 * The Data layer will implement this interface using Firebase SDK.
 */
interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser>
    
    suspend fun registerWithEmail(name: String, email: String, password: String): Result<FirebaseUser>
    
    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser>
    
    suspend fun loginAsGuest(): Result<FirebaseUser>
    
    suspend fun linkGuestToEmail(email: String, password: String): Result<FirebaseUser>
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    fun getCurrentUser(): FirebaseUser?
    
    fun isUserLoggedIn(): Boolean
    
    fun isUserGuest(): Boolean
    
    fun logout()
}
