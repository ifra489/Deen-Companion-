package com.deencompanion.app.di

import com.deencompanion.app.data.repository.AuthRepositoryImpl
import com.deencompanion.app.data.repository.DuaRepositoryImpl
import com.deencompanion.app.data.repository.HomeRepositoryImpl
import com.deencompanion.app.data.repository.PrayerTrackingRepositoryImpl
import com.deencompanion.app.domain.repository.AuthRepository
import com.deencompanion.app.domain.repository.DuaRepository
import com.deencompanion.app.domain.repository.HomeRepository
import com.deencompanion.app.domain.repository.PrayerTrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.deencompanion.app.data.repository.TasbeehRepositoryImpl
import com.deencompanion.app.domain.repository.TasbeehRepository
import com.deencompanion.app.data.repository.HadithRepositoryImpl
import com.deencompanion.app.domain.repository.HadithRepository
import com.deencompanion.app.data.repository.QuranRepositoryImpl
import com.deencompanion.app.domain.repository.QuranRepository
import com.deencompanion.app.data.repository.AzkarRepositoryImpl
import com.deencompanion.app.domain.repository.AzkarRepository
import com.deencompanion.app.data.repository.HabitRepositoryImpl
import com.deencompanion.app.domain.repository.HabitRepository
import com.deencompanion.app.data.repository.GoalRepositoryImpl
import com.deencompanion.app.domain.repository.GoalRepository
import com.deencompanion.app.data.repository.QazaNamazRepositoryImpl
import com.deencompanion.app.domain.repository.QazaNamazRepository
import com.deencompanion.app.data.repository.ZakatRepositoryImpl
import com.deencompanion.app.domain.repository.ZakatRepository
import com.deencompanion.app.data.repository.AchievementRepositoryImpl
import com.deencompanion.app.domain.repository.AchievementRepository
import com.deencompanion.app.data.repository.SettingsRepositoryImpl
import com.deencompanion.app.domain.repository.SettingsRepository
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindZakatRepository(impl: ZakatRepositoryImpl): ZakatRepository



    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository
    @Binds
    @Singleton
    abstract fun bindQazaNamazRepository(impl: QazaNamazRepositoryImpl): QazaNamazRepository
    @Binds
    @Singleton
    abstract fun bindHadithRepository(
        impl: HadithRepositoryImpl
    ): HadithRepository
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository


    @Binds
    @Singleton
    abstract fun bindAzkarRepository(
        impl: AzkarRepositoryImpl
    ): AzkarRepository
    @Binds
    @Singleton
    abstract fun bindQuranRepository(
        quranRepositoryImpl: com.deencompanion.app.data.repository.QuranRepositoryImpl
    ): com.deencompanion.app.domain.repository.QuranRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
    @Binds
    @Singleton
    abstract fun bindTasbeehRepository(
        impl: TasbeehRepositoryImpl
    ): TasbeehRepository
    @Binds
    @Singleton
    abstract fun bindPrayerTrackingRepository(
        impl: PrayerTrackingRepositoryImpl
    ): PrayerTrackingRepository


    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository
    @Binds
    @Singleton
    abstract fun bindDuaRepository(
        impl: DuaRepositoryImpl
    ): DuaRepository
}