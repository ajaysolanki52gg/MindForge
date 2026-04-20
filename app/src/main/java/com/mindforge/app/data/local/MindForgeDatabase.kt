package com.mindforge.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserStatsEntity::class, GameHistoryEntity::class, AchievementEntity::class, CategoryProgressEntity::class],
    version = 4,
    exportSchema = false
)
abstract class MindForgeDatabase : RoomDatabase() {
    abstract fun mindForgeDao(): MindForgeDao
}
