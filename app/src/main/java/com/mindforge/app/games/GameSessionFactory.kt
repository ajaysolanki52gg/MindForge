package com.mindforge.app.games

import com.mindforge.app.domain.model.DailyTask
import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.games.attention.generateReactionTestRound
import com.mindforge.app.games.attention.generateStroopRound
import com.mindforge.app.games.attention.generateTargetTapRound
import com.mindforge.app.games.math.generateLogicalReasoningRound
import com.mindforge.app.games.math.generateNumberSeriesRound
import com.mindforge.app.games.math.generateQuickMathRound
import com.mindforge.app.games.math.generateTimedMentalMathRound
import com.mindforge.app.games.memory.generateNumberRecallRound
import com.mindforge.app.games.memory.generateObjectRecallRound
import com.mindforge.app.games.memory.generatePatternRecallRound
import com.mindforge.app.games.vocabulary.OfflineVocabularyQuestionGenerator
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GameSessionFactory @Inject constructor() {
    private val vocabularyGenerator = OfflineVocabularyQuestionGenerator()

    fun createStandardSession(
        gameType: GameType,
        difficulty: Difficulty,
        randomSeed: Long = System.currentTimeMillis()
    ): GameSession {
        val random = Random(randomSeed)
        val rounds = List(5) { index -> generateRound(gameType, difficulty, index, random) }
        return GameSession(
            title = gameType.title,
            difficulty = difficulty,
            rounds = rounds,
            isDailyChallenge = false
        )
    }

    fun createDailySession(tasks: List<DailyTask>, seed: Long): GameSession {
        val rounds = tasks.map { task ->
            generateRound(
                gameType = task.gameType,
                difficulty = task.difficulty,
                index = task.index,
                random = Random(seed + task.index)
            )
        }
        val challengeDifficulty = tasks.firstOrNull()?.difficulty ?: Difficulty.EASY
        return GameSession(
            title = GameType.DAILY_CHALLENGE.title,
            difficulty = challengeDifficulty,
            rounds = rounds,
            isDailyChallenge = true
        )
    }

    private fun generateRound(
        gameType: GameType,
        difficulty: Difficulty,
        index: Int,
        random: Random
    ): GameRound = when (gameType) {
        GameType.PATTERN_RECALL -> generatePatternRecallRound(index, difficulty, random)
        GameType.NUMBER_RECALL -> generateNumberRecallRound(index, difficulty, random)
        GameType.OBJECT_RECALL -> generateObjectRecallRound(index, difficulty, random)
        GameType.QUICK_MATH -> generateQuickMathRound(index, difficulty, random)
        GameType.NUMBER_SERIES -> generateNumberSeriesRound(index, difficulty, random)
        GameType.LOGICAL_REASONING -> generateLogicalReasoningRound(index, difficulty, random)
        GameType.TIMED_MENTAL_MATH -> generateTimedMentalMathRound(index, difficulty, random)
        GameType.TARGET_TAP -> generateTargetTapRound(index, difficulty, random)
        GameType.REACTION_TEST -> generateReactionTestRound(index, difficulty, random)
        GameType.STROOP_TEST -> generateStroopRound(index, difficulty, random)
        GameType.SYNONYM_ANTONYM -> vocabularyGenerator.synonymAntonym(index, difficulty, random)
        GameType.WORD_MEANING -> vocabularyGenerator.wordMeaning(index, difficulty, random)
        GameType.FILL_IN_THE_BLANK -> vocabularyGenerator.fillInBlank(index, difficulty, random)
        GameType.SPELLING_CORRECTION -> vocabularyGenerator.spellingCorrection(index, difficulty, random)
        GameType.DAILY_CHALLENGE -> error("Daily challenge requires seeded tasks.")
    }
}
