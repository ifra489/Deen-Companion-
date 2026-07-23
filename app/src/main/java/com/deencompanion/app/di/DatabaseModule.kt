




package com.deencompanion.app.di

import com.deencompanion.app.data.local.database.AppDatabase
import android.content.Context
import androidx.room.Room
import com.deencompanion.app.data.local.dao.PrayerRecordDao

import com.deencompanion.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.deencompanion.app.data.local.dao.TasbeehDao
import com.deencompanion.app.data.local.dao.QuranCacheDao
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.deencompanion.app.data.local.dao.HabitDao
import com.deencompanion.app.data.local.dao.GoalDao
import com.deencompanion.app.data.local.dao.QazaNamazDao
import com.deencompanion.app.data.local.dao.BookmarkDao
import com.deencompanion.app.data.local.dao.OfflineCacheDao

/**
 * LEARNING NOTE:
 * This module provides the Room database instance and its DAOs.
 * AppDatabase is a singleton, ensuring only one instance exists throughout the app life.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOfflineCacheDao(database: AppDatabase): OfflineCacheDao = database.offlineCacheDao()

    @Provides
    @Singleton
    fun provideQazaNamazDao(database: AppDatabase): QazaNamazDao = database.qazaNamazDao()

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao = database.habitDao()

    @Provides
    @Singleton
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Provides
    @Singleton
    fun provideQuranCacheDao(database: AppDatabase): QuranCacheDao {
        return database.quranCacheDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePrayerRecordDao(appDatabase: AppDatabase): PrayerRecordDao {
        return appDatabase.prayerRecordDao()
    }

    @Provides
    @Singleton
    fun provideTasbeehDao(appDatabase: AppDatabase): TasbeehDao {
        return appDatabase.tasbeehDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: AppDatabase): com.deencompanion.app.data.local.dao.BookmarkDao = database.bookmarkDao()
}