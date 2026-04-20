package com.mindforge.app.games.math

import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.games.MultipleChoiceRound
import com.mindforge.app.games.TextEntryRound
import kotlin.random.Random

fun generateQuickMathRound(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
    val max = when (difficulty) {
        Difficulty.EASY -> 15
        Difficulty.MEDIUM -> 35
        Difficulty.HARD -> 80
    }
    val a = random.nextInt(2, max)
    val b = random.nextInt(2, max)
    val operator = listOf("+", "-", "*").random(random)
    val answer = when (operator) {
        "+" -> a + b
        "-" -> a - b
        else -> a * b
    }
    val options = listOf(answer, answer + 2, answer - 3, answer + 5).distinct().shuffled(random)
    return MultipleChoiceRound(
        id = "quick-math-$index",
        gameType = GameType.QUICK_MATH,
        prompt = "$a $operator $b = ?",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 12
            Difficulty.MEDIUM -> 10
            Difficulty.HARD -> 8
        },
        options = options.map(Int::toString),
        correctIndex = options.indexOf(answer)
    )
}

fun generateNumberSeriesRound(index: Int, difficulty: Difficulty, random: Random): TextEntryRound {
    val patternType = random.nextInt(3)
    val sequence = when (patternType) {
        0 -> {
            val start = random.nextInt(2, 12)
            val step = random.nextInt(2, if (difficulty == Difficulty.HARD) 10 else 6)
            List(4) { start + (it * step) }
        }
        1 -> {
            val start = random.nextInt(2, 5)
            val ratio = if (difficulty == Difficulty.EASY) 2 else random.nextInt(2, 4)
            List(4) { start * ratio.toDouble().pow(it).toInt() }
        }
        else -> {
            val start = random.nextInt(3, 8)
            List(4) { start + (it * it) }
        }
    }
    val answer = when (patternType) {
        0 -> sequence.last() + (sequence[1] - sequence[0])
        1 -> sequence.last() * (sequence[1] / sequence[0])
        else -> sequence.last() + 7
    }
    return TextEntryRound(
        id = "series-$index",
        gameType = GameType.NUMBER_SERIES,
        prompt = "Find the next number: ${sequence.joinToString(", ")}",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 16
            Difficulty.MEDIUM -> 13
            Difficulty.HARD -> 10
        },
        answer = answer.toString(),
        supportingText = "Look for arithmetic, geometric, or mixed growth."
    )
}

fun generateLogicalReasoningRound(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
    val groups = listOf(
        listOf("Rose", "Lily", "Tulip", "Hammer") to "Hammer",
        listOf("Mercury", "Venus", "Mars", "Map") to "Map",
        listOf("Violin", "Flute", "Drum", "Bread") to "Bread",
        listOf("Square", "Triangle", "Circle", "Carrot") to "Carrot"
    )
    val (options, answer) = groups.random(random)
    return MultipleChoiceRound(
        id = "logic-$index",
        gameType = GameType.LOGICAL_REASONING,
        prompt = if (difficulty == Difficulty.HARD) {
            "Select the odd one out."
        } else {
            "Which item does not belong with the others?"
        },
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 15
            Difficulty.MEDIUM -> 12
            Difficulty.HARD -> 9
        },
        options = options.shuffled(random),
        correctIndex = options.shuffled(random).indexOf(answer)
    ).let {
        val shuffled = options.shuffled(random)
        it.copy(options = shuffled, correctIndex = shuffled.indexOf(answer))
    }
}

fun generateTimedMentalMathRound(index: Int, difficulty: Difficulty, random: Random): TextEntryRound {
    val max = when (difficulty) {
        Difficulty.EASY -> 20
        Difficulty.MEDIUM -> 45
        Difficulty.HARD -> 90
    }
    val a = random.nextInt(10, max)
    val b = random.nextInt(5, max)
    val c = random.nextInt(2, 12)
    val answer = (a + b) - c
    return TextEntryRound(
        id = "timed-mental-$index",
        gameType = GameType.TIMED_MENTAL_MATH,
        prompt = "Compute quickly: ($a + $b) - $c",
        timeLimitSeconds = when (difficulty) {
            Difficulty.EASY -> 10
            Difficulty.MEDIUM -> 8
            Difficulty.HARD -> 6
        },
        answer = answer.toString(),
        supportingText = "Speed matters here, but accuracy still wins."
    )
}

private fun Double.pow(exponent: Int): Double = Math.pow(this, exponent.toDouble())
