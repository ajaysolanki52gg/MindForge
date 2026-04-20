package com.mindforge.app.domain.model

data class UserStats(
    val id: Int = 1,
    val streak: Int = 0,
    val lastPlayedDate: String? = null,
    val weeklyActivity: List<Boolean> = List(7) { false }
)

data class GameHistory(
    val id: Int = 0,
    val gameType: String,
    val score: Int,
    val difficulty: String,
    val timestamp: Long
)

data class Achievement(
    val id: Int = 0,
    val title: String,
    val unlocked: Boolean
)

data class CategoryProgress(
    val category: Category,
    val streak: Int = 0,
    val lastPlayedDate: String? = null,
    val weeklyActivity: List<Boolean> = List(7) { false }
)

data class DailyTask(
    val index: Int,
    val gameType: GameType,
    val difficulty: Difficulty
)

data class RewardSummary(
    val score: Int,
    val accuracyPercent: Int,
    val streak: Int,
    val dailyBonusAwarded: Boolean,
    val rank: Int = 0
)
