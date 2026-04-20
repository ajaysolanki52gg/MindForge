package com.mindforge.app.games

import androidx.compose.ui.graphics.Color
import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType

data class GameSession(
    val title: String,
    val difficulty: Difficulty,
    val rounds: List<GameRound>,
    val isDailyChallenge: Boolean
)

sealed interface GameRound {
    val id: String
    val gameType: GameType
    val prompt: String
    val timeLimitSeconds: Int
}

data class MultipleChoiceRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val options: List<String>,
    val correctIndex: Int,
    val supportingText: String? = null,
    val revealText: String? = null,
    val revealMillis: Long = 0
) : GameRound

data class TextEntryRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val answer: String,
    val supportingText: String? = null,
    val revealText: String? = null,
    val revealMillis: Long = 0
) : GameRound

data class PatternRecallRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val gridSize: Int,
    val highlightedCells: Set<Int>,
    val revealMillis: Long = 2000
) : GameRound

data class TapTargetRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val items: List<String>,
    val targetIndex: Int,
    val columns: Int
) : GameRound

data class ReactionRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val waitMillis: Long,
    val idealMillis: Long = 450L
) : GameRound

data class StroopRound(
    override val id: String,
    override val gameType: GameType,
    override val prompt: String,
    override val timeLimitSeconds: Int,
    val word: String,
    val inkColor: Color,
    val correctColorName: String,
    val options: List<String>
) : GameRound

data class SessionOutcome(
    val score: Int,
    val correctAnswers: Int,
    val totalRounds: Int
)
