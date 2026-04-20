package com.mindforge.app.domain.usecase

import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.domain.model.RewardSummary
import com.mindforge.app.domain.repository.MindForgeRepository
import javax.inject.Inject

class SubmitGameResultUseCase @Inject constructor(
    private val repository: MindForgeRepository
) {
    suspend operator fun invoke(
        gameType: GameType,
        difficulty: Difficulty,
        score: Int,
        accuracyPercent: Int,
        isDailyChallenge: Boolean
    ): RewardSummary = repository.recordResult(
        gameType = gameType,
        difficulty = difficulty,
        score = score,
        accuracyPercent = accuracyPercent,
        isDailyChallenge = isDailyChallenge
    )
}
