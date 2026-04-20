package com.mindforge.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val streak: Int = 0,
    val lastPlayedDate: String? = null,
    val weeklyActivity: String = "0,0,0,0,0,0,0" // Comma separated booleans
)
