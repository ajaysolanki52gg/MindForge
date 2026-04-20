package com.mindforge.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameType: String,
    val score: Int,
    val difficulty: String,
    val timestamp: Long
)
