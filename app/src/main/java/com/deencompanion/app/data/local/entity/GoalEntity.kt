package com.deencompanion.app.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val progressCurrent: Int,
    val progressTarget: Int,
    val createdAt: Long
)