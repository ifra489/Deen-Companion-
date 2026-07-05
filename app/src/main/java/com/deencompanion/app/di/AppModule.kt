package com.deencompanion.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.deencompanion.app.data.local.database.AppDatabase
import com.deencompanion.app.data.remote.api.AladhanApi
import com.deencompanion.app.data.remote.api.QuranApi
import com.deencompanion.app.data.remote.api.QuranWordApi
import com.deencompanion.app.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * LEARNING NOTE:
 * This Hilt module provides application-wide, long-lived singleton dependencies (Application Context, DataStore, API services).
 * By using @InstallIn(SingletonComponent::class), these dependencies will live as long as the application process.
 * ViewModels, use cases, and repositories will request these dependencies by injecting them in their constructors.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(Constants.DATASTORE_NAME) }
        )
    }
    @Provides
    @Singleton
    fun provideQuranWordApi(
        @Named("quranword") retrofit: Retrofit
    ): QuranWordApi {
        return retrofit.create(QuranWordApi::class.java)
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        val webClientId = context.getString(Constants.WEB_CLIENT_ID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideAladhanApi(
        @Named("aladhan") retrofit: Retrofit
    ): AladhanApi {
        return retrofit.create(AladhanApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuranApi(
        @Named("alquran") retrofit: Retrofit
    ): QuranApi {
        return retrofit.create(QuranApi::class.java)
    }

    // LEARNING NOTE:
    // API services and general app components are provided here.
    // Database-related dependencies are provided in DatabaseModule.kt.
}
