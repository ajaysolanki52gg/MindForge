package com.mindforge.app.domain.repository

import com.mindforge.app.domain.model.Achievement
import com.mindforge.app.domain.model.Category
import com.mindforge.app.domain.model.CategoryProgress
import com.mindforge.app.domain.model.DailyTask
import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameHistory
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.domain.model.RewardSummary
import com.mindforge.app.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface MindForgeRepository {
    fun observeUserStats(): Flow<UserStats>
    fun observeGameHistory(): Flow<List<GameHistory>>
    fun observeAchievements(): Flow<List<Achievement>>
    fun observeCategoryProgress(): Flow<List<CategoryProgress>>
    fun observeTopScores(): Flow<List<GameHistory>>
    fun observeTopScoresForGame(gameId: String): Flow<List<GameHistory>>
    fun observeFilteredTopScores(gameId: String?, difficulty: String?): Flow<List<GameHistory>>
    suspend fun getHighScoreForGame(gameId: String): Int
    suspend fun ensureSeedData()
    fun getCategoryGames(category: Category): List<GameType>
    fun getDailyChallenge(dateKey: String): List<DailyTask>
    suspend fun recordResult(
        gameType: GameType,
        difficulty: Difficulty,
        score: Int,
        accuracyPercent: Int,
        isDailyChallenge: Boolean
    ): RewardSummary
}
