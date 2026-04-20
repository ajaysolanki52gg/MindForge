package com.mindforge.app.domain.usecase

import com.mindforge.app.domain.model.DailyTask
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.utils.DateUtils
import javax.inject.Inject

class GenerateDailyChallengeUseCase @Inject constructor(
    private val repository: MindForgeRepository
) {
    operator fun invoke(dateKey: String = DateUtils.todayKey()): List<DailyTask> =
        repository.getDailyChallenge(dateKey)
}
