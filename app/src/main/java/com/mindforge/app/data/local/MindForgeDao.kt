package com.mindforge.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MindForgeDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun observeUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun observeGameHistory(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history ORDER BY score DESC LIMIT 10")
    fun observeTopScores(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history WHERE gameType = :gameType ORDER BY score DESC LIMIT 10")
    fun observeTopScoresForGame(gameType: String): Flow<List<GameHistoryEntity>>

    @Query("""
        SELECT * FROM game_history 
        WHERE (:gameId IS NULL OR gameType = :gameId) 
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY score DESC LIMIT 20
    """)
    fun observeFilteredTopScores(gameId: String?, difficulty: String?): Flow<List<GameHistoryEntity>>

    @Query("SELECT score FROM game_history WHERE gameType = :gameType AND difficulty = :difficulty")
    suspend fun getAllScoresForGameAndDifficulty(gameType: String, difficulty: String): List<Int>

    @Query("SELECT MAX(score) FROM game_history WHERE gameType = :gameType")
    suspend fun getHighScoreForGame(gameType: String): Int?

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM category_progress ORDER BY categoryId ASC")
    fun observeCategoryProgress(): Flow<List<CategoryProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultStats(stats: UserStatsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(items: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryProgress(items: List<CategoryProgressEntity>)

    @Update
    suspend fun updateStats(stats: UserStatsEntity)

    @Update
    suspend fun updateAchievement(item: AchievementEntity)

    @Update
    suspend fun updateCategoryProgress(item: CategoryProgressEntity)

    @Insert
    suspend fun insertGameHistory(item: GameHistoryEntity)

    @Query("SELECT * FROM category_progress WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getCategoryProgressById(categoryId: String): CategoryProgressEntity?
}
