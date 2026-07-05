package com.deencompanion.app.data.repository

import com.deencompanion.app.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LEARNING NOTE:
 * AuthRepositoryImpl implements AuthRepository in the Data layer.
 * It uses FirebaseAuth for sign-in/registration sessions and Firestore to persist user profile documents.
 * All functions execute on Dispatchers.IO to avoid blocking the main UI thread.
 * Firebase exceptions are mapped to user-friendly messages for a premium UX.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user ?: throw Exception("User is null after sign in")
            }.mapFailure { mapFirebaseException(it) }
        }

    override suspend fun registerWithEmail(
        name: String,
        email: String,
        password: String
    ): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        runCatching {
            // 1. Create email/password user in Firebase Auth
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null after creation")

            // 2. Set user display name in Firebase Auth
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user.updateProfile(profileUpdates).await()

            // 3. Write user metadata to Cloud Firestore users/{uid}
            val userDoc = mapOf(
                "name" to name,
                "email" to email,
                "isGuest" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(user.uid).set(userDoc).await()

            user
        }.mapFailure { mapFirebaseException(it) }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                val user = result.user ?: throw Exception("User is null after Google sign in")

                // Check if user doc exists in Firestore, create if missing
                val userDocRef = firestore.collection("users").document(user.uid)
                val document = userDocRef.get().await()
                if (!document.exists()) {
                    val userDoc = mapOf(
                        "name" to (user.displayName ?: "Google User"),
                        "email" to (user.email ?: ""),
                        "isGuest" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    userDocRef.set(userDoc).await()
                }

                user
            }.mapFailure { mapFirebaseException(it) }
        }

    override suspend fun loginAsGuest(): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = firebaseAuth.signInAnonymously().await()
                val user = result.user ?: throw Exception("User is null after Guest sign in")

                // Write guest details to Firestore
                val userDoc = mapOf(
                    "name" to "Guest",
                    "email" to "",
                    "isGuest" to true,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("users").document(user.uid).set(userDoc).await()

                user
            }.mapFailure { mapFirebaseException(it) }
        }

    override suspend fun linkGuestToEmail(email: String, password: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            runCatching {
                val currentUser = firebaseAuth.currentUser ?: throw Exception("No active guest session found")
                val credential = EmailAuthProvider.getCredential(email, password)
                
                // Link credential to anonymous account
                val result = currentUser.linkWithCredential(credential).await()
                val user = result.user ?: throw Exception("User is null after account link")

                // Update isGuest status to false in Firestore user document
                val updates = mapOf(
                    "email" to email,
                    "isGuest" to false
                )
                firestore.collection("users").document(user.uid).update(updates).await()

                user
            }.mapFailure { mapFirebaseException(it) }
        }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Unit
            }.mapFailure { mapFirebaseException(it) }
        }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun isUserGuest(): Boolean {
        return firebaseAuth.currentUser?.isAnonymous == true
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    // Helper inline extension to map Result failure exceptions
    private inline fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> {
        val exception = exceptionOrNull()
        return if (exception != null) {
            Result.failure(transform(exception))
        } else {
            this
        }
    }

    // Maps Firebase SDK exceptions to clean user-friendly messages
    private fun mapFirebaseException(throwable: Throwable): Throwable {
        if (throwable is FirebaseAuthException) {
            val friendlyMessage = when (throwable) {
                is FirebaseAuthInvalidUserException -> "No account found matching this email address."
                is FirebaseAuthInvalidCredentialsException -> "Incorrect password or authentication credentials."
                is FirebaseAuthUserCollisionException -> "An account with this email address already exists."
                is FirebaseAuthWeakPasswordException -> "The password provided is too weak."
                else -> throwable.localizedMessage ?: "Authentication failed. Please try again."
            }
            return Exception(friendlyMessage, throwable)
        }
        if (throwable.javaClass.simpleName.contains("FirebaseNetworkException")) {
            return Exception("Network connection lost. Please check your internet connection and try again.", throwable)
        }
        return throwable
    }
}
