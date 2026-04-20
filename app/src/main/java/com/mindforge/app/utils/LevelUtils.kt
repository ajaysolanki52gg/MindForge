package com.mindforge.app.utils

import com.mindforge.app.domain.model.Difficulty
import kotlin.math.sqrt

object LevelUtils {
    fun levelFromXp(xp: Int): Int = sqrt(xp / 100f).toInt()

    fun isDifficultyUnlocked(difficulty: Difficulty, xp: Int): Boolean = true

    fun highestUnlocked(xp: Int): Difficulty = Difficulty.HARD
}
