package com.mindforge.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_progress")
data class CategoryProgressEntity(
    @PrimaryKey val categoryId: String,
    val streak: Int = 0,
    val lastPlayedDate: String? = null,
    val weeklyActivity: String = "0,0,0,0,0,0,0"
)
