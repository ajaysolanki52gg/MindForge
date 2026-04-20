package com.mindforge.app.ui.navigation

object Destinations {
    const val HOME = "home"
    const val STATS = "stats"
    const val CATEGORY = "category/{categoryId}"
    const val GAME = "game/{gameId}/{difficulty}"
    const val RESULT = "result/{gameId}/{difficulty}/{score}/{accuracy}/{streak}/{dailyBonus}/{rank}"

    fun category(categoryId: String): String = "category/$categoryId"

    fun game(gameId: String, difficulty: String): String = "game/$gameId/$difficulty"

    fun result(
        gameId: String,
        difficulty: String,
        score: Int,
        accuracy: Int,
        streak: Int,
        dailyBonus: Boolean,
        rank: Int
    ): String = "result/$gameId/$difficulty/$score/$accuracy/$streak/$dailyBonus/$rank"
}
