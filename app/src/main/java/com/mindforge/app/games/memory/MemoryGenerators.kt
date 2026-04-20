package com.mindforge.app.games.memory

import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.games.MultipleChoiceRound
import com.mindforge.app.games.PatternRecallRound
import com.mindforge.app.games.TextEntryRound
import kotlin.random.Random

private val objectPool = listOf(
    "Clock", "Leaf", "Star", "Book", "Moon", "Chair", "Bottle", "Key",
    "Apple", "Cloud", "Bell", "Bridge", "Comet", "Anchor", "Pencil"
)

fun generatePatternRecallRound(index: Int, difficulty: Difficulty, random: Random): PatternRecallRound {
    val gridSize = when (difficulty) {
        Difficulty.EASY -> 3
        Difficulty.MEDIUM -> 4
        Difficulty.HARD -> 6
    }
    val highlightCount = when (difficulty) {
        Difficulty.EASY -> 3
        Difficulty.MEDIUM -> 5
        Difficulty.HARD -> 8
    }
    val cells = mutableSetOf<Int>()
    while (cells.size < highlightCount) {
        cells += random.nextInt(gridSize * gridSize)
    }
    return PatternRecallRound(
        id = "pattern-$index",
        gameType = GameType.PATTERN_RECALL,
        prompt = "Memorize the glowing pattern, then tap the same cells.",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 12
            Difficulty.MEDIUM -> 10
            Difficulty.HARD -> 8
        },
        gridSize = gridSize,
        highlightedCells = cells,
        revealMillis = 2000
    )
}

fun generateNumberRecallRound(index: Int, difficulty: Difficulty, random: Random): TextEntryRound {
    val length = when (difficulty) {
        Difficulty.EASY -> 4
        Difficulty.MEDIUM -> 6
        Difficulty.HARD -> 8
    }
    val sequence = buildString {
        repeat(length) { append(random.nextInt(0, 10)) }
    }
    return TextEntryRound(
        id = "number-$index",
        gameType = GameType.NUMBER_RECALL,
        prompt = "Enter the number sequence exactly as shown.",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 14
            Difficulty.MEDIUM -> 12
            Difficulty.HARD -> 10
        },
        answer = sequence,
        revealText = sequence,
        revealMillis = when (difficulty) {
            Difficulty.EASY -> 2600
            Difficulty.MEDIUM -> 2200
            Difficulty.HARD -> 1800
        }
    )
}

fun generateObjectRecallRound(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
    val shownCount = when (difficulty) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 7
        Difficulty.HARD -> 9
    }
    val shownItems = objectPool.shuffled(random).take(shownCount)
    val correct = shownItems.random(random)
    val wrongItems = objectPool.filterNot { it in shownItems }.shuffled(random).take(3)
    val options = (wrongItems + correct).shuffled(random)
    return MultipleChoiceRound(
        id = "object-$index",
        gameType = GameType.OBJECT_RECALL,
        prompt = "Which item was present in the memory tray?",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 14
            Difficulty.MEDIUM -> 12
            Difficulty.HARD -> 10
        },
        options = options,
        correctIndex = options.indexOf(correct),
        revealText = shownItems.joinToString("  "),
        revealMillis = 2600
    )
}
