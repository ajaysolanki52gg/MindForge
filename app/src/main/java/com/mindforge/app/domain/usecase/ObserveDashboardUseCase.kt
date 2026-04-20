package com.mindforge.app.domain.usecase

import com.mindforge.app.domain.model.CategoryOverview
import com.mindforge.app.domain.model.DashboardState
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.utils.DateUtils
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveDashboardUseCase @Inject constructor(
    private val repository: MindForgeRepository
) {
    operator fun invoke(): Flow<DashboardState> = combine(
        repository.observeUserStats(),
        repository.observeAchievements(),
        repository.observeCategoryProgress()
    ) { stats, achievements, categoryProgress ->
        val progressById = categoryProgress.associateBy { it.category.id }
        DashboardState(
            stats = stats,
            categories = com.mindforge.app.domain.model.Category.entries.map {
                CategoryOverview(
                    category = it,
                    games = repository.getCategoryGames(it),
                    progress = progressById[it.id] ?: com.mindforge.app.domain.model.CategoryProgress(it)
                )
            },
            dailyTasks = repository.getDailyChallenge(DateUtils.todayKey()),
            achievementsUnlocked = achievements.count { it.unlocked }
        )
    }
}
