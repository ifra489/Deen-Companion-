package com.deencompanion.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deencompanion.app.data.local.dao.PrayerRecordDao
import com.deencompanion.app.data.local.dao.QuranCacheDao
import com.deencompanion.app.data.local.dao.TasbeehDao
import com.deencompanion.app.data.local.entity.CachedSurahEntity
import com.deencompanion.app.data.local.entity.PrayerRecordEntity
import com.deencompanion.app.data.local.entity.TasbeehCountEntity
import com.deencompanion.app.util.Constants
import com.deencompanion.app.data.local.dao.HabitDao
import com.deencompanion.app.data.local.dao.GoalDao
import com.deencompanion.app.data.local.entity.HabitEntity
import com.deencompanion.app.data.local.entity.HabitCompletionEntity
import com.deencompanion.app.data.local.entity.GoalEntity
import com.deencompanion.app.data.local.dao.QazaNamazDao
import com.deencompanion.app.data.local.entity.QazaPrayerEntity
import com.deencompanion.app.data.local.entity.QazaSettingsEntity
@Database(
    entities = [
        PrayerRecordEntity::class,
        TasbeehCountEntity::class,
        CachedSurahEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        GoalEntity::class,
                QazaPrayerEntity::class,
        QazaSettingsEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerRecordDao(): PrayerRecordDao
    abstract fun tasbeehDao(): TasbeehDao
    abstract fun quranCacheDao(): QuranCacheDao
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun qazaNamazDao(): QazaNamazDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}