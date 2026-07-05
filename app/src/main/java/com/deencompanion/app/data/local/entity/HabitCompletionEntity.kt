package com.deencompanion.app.data.local.entity



import androidx.room.Entity

@Entity(tableName = "habit_completions", primaryKeys = ["habitId", "date"])
data class HabitCompletionEntity(
    val habitId: String,
    val date: String, // "yyyy-MM-dd"
    val completed: Boolean
)