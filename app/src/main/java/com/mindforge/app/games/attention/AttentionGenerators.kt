package com.mindforge.app.games.attention

import androidx.compose.ui.graphics.Color
import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.games.ReactionRound
import com.mindforge.app.games.StroopRound
import com.mindforge.app.games.TapTargetRound
import kotlin.random.Random

fun generateTargetTapRound(index: Int, difficulty: Difficulty, random: Random): TapTargetRound {
    val gridSize = when (difficulty) {
        Difficulty.EASY -> 9
        Difficulty.MEDIUM -> 16
        Difficulty.HARD -> 20
    }
    val targetWord = listOf("MOON", "BOLT", "STAR", "WAVE", "MINT").random(random)
    val distractorWord = listOf("STAR", "MIST", "MUTE", "WARM", "BARK").filterNot { it == targetWord }.random(random)
    val targetIndex = random.nextInt(gridSize)
    val items = List(gridSize) { indexValue ->
        if (indexValue == targetIndex) targetWord else distractorWord
    }
    return TapTargetRound(
        id = "target-$index",
        gameType = GameType.TARGET_TAP,
        prompt = "Tap \"$targetWord\" as fast as you can.",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 10
            Difficulty.MEDIUM -> 8
            Difficulty.HARD -> 6
        },
        items = items,
        targetIndex = targetIndex,
        columns = if (gridSize <= 9) 3 else 4
    )
}

fun generateReactionTestRound(index: Int, difficulty: Difficulty, random: Random): ReactionRound =
    ReactionRound(
        id = "reaction-$index",
        gameType = GameType.REACTION_TEST,
        prompt = "Wait for the panel to turn green, then tap immediately.",
        timeLimitSeconds = 6,
        waitMillis = when (difficulty) {
            Difficulty.EASY -> random.nextLong(1200L, 2200L)
            Difficulty.MEDIUM -> random.nextLong(1500L, 2600L)
            Difficulty.HARD -> random.nextLong(1800L, 3200L)
        }
    )

fun generateStroopRound(index: Int, difficulty: Difficulty, random: Random): StroopRound {
    val colors = listOf(
        "Red" to Color(0xFFD54B4B),
        "Blue" to Color(0xFF4B7BF5),
        "Green" to Color(0xFF37A76F),
        "Orange" to Color(0xFFF3A33C)
    )
    val correct = colors.random(random)
    val word = colors.filterNot { it.first == correct.first }.random(random).first
    val options = colors.map { it.first }.shuffled(random)
    return StroopRound(
        id = "stroop-$index",
        gameType = GameType.STROOP_TEST,
        prompt = if (difficulty == Difficulty.HARD) "Pick the ink color." else "Ignore the word. Choose the ink color.",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 12
            Difficulty.MEDIUM -> 10
            Difficulty.HARD -> 8
        },
        word = word,
        inkColor = correct.second,
        correctColorName = correct.first,
        options = options
    )
}
