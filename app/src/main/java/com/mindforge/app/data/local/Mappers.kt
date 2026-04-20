package com.mindforge.app.data.local

import com.mindforge.app.domain.model.Achievement
import com.mindforge.app.domain.model.Category
import com.mindforge.app.domain.model.CategoryProgress
import com.mindforge.app.domain.model.GameHistory
import com.mindforge.app.domain.model.UserStats

fun UserStatsEntity.toDomain(): UserStats = UserStats(
    id = id,
    streak = streak,
    lastPlayedDate = lastPlayedDate,
    weeklyActivity = weeklyActivity.split(",").map { it == "1" }
)

fun GameHistoryEntity.toDomain(): GameHistory = GameHistory(
    id = id,
    gameType = gameType,
    score = score,
    difficulty = difficulty,
    timestamp = timestamp
)

fun AchievementEntity.toDomain(): Achievement = Achievement(
    id = id,
    title = title,
    unlocked = unlocked
)

fun CategoryProgressEntity.toDomain(): CategoryProgress = CategoryProgress(
    category = Category.fromId(categoryId),
    streak = streak,
    lastPlayedDate = lastPlayedDate,
    weeklyActivity = weeklyActivity.split(",").map { it == "1" }
)
