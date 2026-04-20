package com.mindforge.app.domain.model

data class CategoryOverview(
    val category: Category,
    val games: List<GameType>,
    val progress: CategoryProgress = CategoryProgress(category)
)

data class DashboardState(
    val stats: UserStats = UserStats(),
    val categories: List<CategoryOverview> = Category.entries.map {
        CategoryOverview(it, GameType.forCategory(it))
    },
    val dailyTasks: List<DailyTask> = emptyList(),
    val achievementsUnlocked: Int = 0
)
