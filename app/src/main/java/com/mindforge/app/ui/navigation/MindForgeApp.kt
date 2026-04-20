package com.mindforge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mindforge.app.ui.screens.CategoryRoute
import com.mindforge.app.ui.screens.GameRoute
import com.mindforge.app.ui.screens.HomeRoute
import com.mindforge.app.ui.screens.ResultRoute
import com.mindforge.app.ui.screens.StatsRoute

@Composable
fun MindForgeApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.HOME,
        modifier = modifier
    ) {
        composable(Destinations.HOME) {
            HomeRoute(
                viewModel = hiltViewModel(),
                onCategoryClick = { categoryId -> navController.navigate(Destinations.category(categoryId)) },
                onDailyChallengeClick = { navController.navigate(Destinations.game("daily-challenge", "medium")) },
                onStatsClick = { navController.navigate(Destinations.STATS) }
            )
        }
        composable(
            route = Destinations.CATEGORY,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            CategoryRoute(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() },
                onOpenGame = { gameId, difficulty -> navController.navigate(Destinations.game(gameId, difficulty)) }
            )
        }
        composable(
            route = Destinations.GAME,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) {
            GameRoute(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() },
                onFinished = { gameId, difficulty, result ->
                    navController.navigate(
                        Destinations.result(
                            gameId = gameId,
                            difficulty = difficulty,
                            score = result.score,
                            accuracy = result.accuracyPercent,
                            streak = result.streak,
                            dailyBonus = result.dailyBonusAwarded,
                            rank = result.rank
                        )
                    ) {
                        popUpTo(Destinations.HOME)
                    }
                }
            )
        }
        composable(
            route = Destinations.RESULT,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
                navArgument("accuracy") { type = NavType.IntType },
                navArgument("streak") { type = NavType.IntType },
                navArgument("dailyBonus") { type = NavType.BoolType },
                navArgument("rank") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            ResultRoute(
                gameId = backStackEntry.arguments?.getString("gameId").orEmpty(),
                difficulty = backStackEntry.arguments?.getString("difficulty").orEmpty(),
                score = backStackEntry.arguments?.getInt("score") ?: 0,
                accuracy = backStackEntry.arguments?.getInt("accuracy") ?: 0,
                streak = backStackEntry.arguments?.getInt("streak") ?: 0,
                dailyBonus = backStackEntry.arguments?.getBoolean("dailyBonus") ?: false,
                rank = backStackEntry.arguments?.getInt("rank") ?: 0,
                onDone = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.STATS) {
            StatsRoute(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
