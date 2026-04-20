package com.mindforge.app.data.repository

import com.mindforge.app.data.local.*
import com.mindforge.app.domain.model.*
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.utils.DateUtils
import com.mindforge.app.utils.SeedUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

@Singleton
class OfflineMindForgeRepository @Inject constructor(
    private val dao: MindForgeDao
) : MindForgeRepository {

    override fun observeUserStats(): Flow<UserStats> =
        dao.observeUserStats().map { it?.toDomain() ?: UserStats() }

    override fun observeGameHistory(): Flow<List<GameHistory>> =
        dao.observeGameHistory().map { items -> items.map { it.toDomain() } }

    override fun observeTopScores(): Flow<List<GameHistory>> =
        dao.observeTopScores().map { items -> items.map { it.toDomain() } }

    override fun observeTopScoresForGame(gameId: String): Flow<List<GameHistory>> =
        dao.observeTopScoresForGame(gameId).map { items -> items.map { it.toDomain() } }

    override fun observeFilteredTopScores(gameId: String?, difficulty: String?): Flow<List<GameHistory>> =
        dao.observeFilteredTopScores(gameId, difficulty).map { items -> items.map { it.toDomain() } }

    override suspend fun getHighScoreForGame(gameId: String): Int =
        dao.getHighScoreForGame(gameId) ?: 0

    override suspend fun ensureSeedData() {
        dao.insertDefaultStats(UserStatsEntity())
        dao.insertAchievements(defaultAchievements())
        dao.insertCategoryProgress(defaultCategoryProgress())
    }

    override fun getCategoryGames(category: Category): List<GameType> = GameType.forCategory(category)

    override fun getDailyChallenge(dateKey: String): List<DailyTask> {
        val seed = SeedUtils.seedFor("daily", dateKey)
        val random = Random(seed)
        val baseTasks = Category.entries.mapIndexed { index, category ->
            DailyTask(
                index = index,
                gameType = getCategoryGames(category).random(random),
                difficulty = Difficulty.MEDIUM
            )
        }.toMutableList()

        val bonusPool = GameType.entries.filter { it.category != null }
        baseTasks += DailyTask(
            index = baseTasks.size,
            gameType = bonusPool.random(random),
            difficulty = Difficulty.MEDIUM
        )
        return baseTasks
    }

    override suspend fun recordResult(
        gameType: GameType,
        difficulty: Difficulty,
        score: Int,
        accuracyPercent: Int,
        isDailyChallenge: Boolean
    ): RewardSummary {
        ensureSeedData()
        val currentStats = dao.observeUserStats().first() ?: UserStatsEntity()
        val today = DateUtils.todayKey()
        
        val nextStreak = when (currentStats.lastPlayedDate) {
            today -> currentStats.streak
            DateUtils.yesterdayKey() -> currentStats.streak + 1
            else -> 1
        }

        val dayOfWeekIndex = getDayOfWeekIndex()
        val updatedWeekly = updateWeeklyActivity(currentStats.weeklyActivity, dayOfWeekIndex)

        val updatedStats = currentStats.copy(
            streak = nextStreak,
            lastPlayedDate = today,
            weeklyActivity = updatedWeekly
        )
        dao.updateStats(updatedStats)
        
        dao.insertGameHistory(
            GameHistoryEntity(
                gameType = gameType.id,
                score = score,
                difficulty = difficulty.id,
                timestamp = System.currentTimeMillis()
            )
        )
        
        // Calculate Rank
        val allScores = dao.getAllScoresForGameAndDifficulty(gameType.id, difficulty.id)
        val rank = allScores.count { it > score } + 1

        updateCategoryStreaks(gameType = gameType, today = today, dayOfWeekIndex = dayOfWeekIndex)
        unlockAchievements(updatedStats)

        return RewardSummary(
            score = score,
            accuracyPercent = accuracyPercent,
            streak = updatedStats.streak,
            dailyBonusAwarded = isDailyChallenge && currentStats.lastPlayedDate != today,
            rank = rank
        )
    }

    private fun getDayOfWeekIndex(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    private fun updateWeeklyActivity(current: String, index: Int): String {
        val parts = current.split(",").toMutableList()
        if (index in 0..6) {
            parts[index] = "1"
        }
        return parts.joinToString(",")
    }

    private suspend fun unlockAchievements(stats: UserStatsEntity) {
        val achievements = dao.observeAchievements().first()
        achievements.forEach { item ->
            val shouldUnlock = when (item.title) {
                "First Spark" -> true
                "Focused Mind" -> stats.streak >= 3
                "Forge Master" -> stats.streak >= 7
                else -> false
            }
            if (shouldUnlock && !item.unlocked) {
                dao.updateAchievement(item.copy(unlocked = true))
            }
        }
    }

    private suspend fun updateCategoryStreaks(gameType: GameType, today: String, dayOfWeekIndex: Int) {
        val categoriesToUpdate = when (gameType) {
            GameType.DAILY_CHALLENGE -> Category.entries
            else -> listOfNotNull(gameType.category)
        }
        categoriesToUpdate.forEach { category ->
            val current = dao.getCategoryProgressById(category.id)
                ?: CategoryProgressEntity(categoryId = category.id)
            val nextStreak = when (current.lastPlayedDate) {
                today -> current.streak
                DateUtils.yesterdayKey() -> current.streak + 1
                else -> 1
            }
            dao.updateCategoryProgress(
                current.copy(
                    streak = nextStreak,
                    lastPlayedDate = today,
                    weeklyActivity = updateWeeklyActivity(current.weeklyActivity, dayOfWeekIndex)
                )
            )
        }
    }

    override fun observeAchievements(): Flow<List<Achievement>> =
        dao.observeAchievements().map { items -> items.map { it.toDomain() } }

    override fun observeCategoryProgress(): Flow<List<CategoryProgress>> =
        dao.observeCategoryProgress().map { items -> items.map { it.toDomain() } }

    private fun defaultAchievements(): List<AchievementEntity> = listOf(
        AchievementEntity(title = "First Spark"),
        AchievementEntity(title = "Focused Mind"),
        AchievementEntity(title = "Forge Master")
    )

    private fun defaultCategoryProgress(): List<CategoryProgressEntity> =
        Category.entries.map { category ->
            CategoryProgressEntity(categoryId = category.id)
        }
}
