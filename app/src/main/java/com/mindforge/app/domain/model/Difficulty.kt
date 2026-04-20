package com.mindforge.app.domain.model

enum class Difficulty(
    val id: String,
    val label: String,
    val timeBonusMultiplier: Float
) {
    EASY("easy", "Easy", 1.0f),
    MEDIUM("medium", "Medium", 1.35f),
    HARD("hard", "Hard", 1.7f);

    companion object {
        fun fromId(id: String): Difficulty = entries.firstOrNull { it.id == id } ?: EASY
    }
}
