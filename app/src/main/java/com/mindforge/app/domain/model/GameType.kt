package com.mindforge.app.domain.model

enum class GameType(
    val id: String,
    val title: String,
    val description: String,
    val category: Category?
) {
    DAILY_CHALLENGE("daily-challenge", "Daily Challenge", "Five seeded tasks with bonus XP", null),

    PATTERN_RECALL("pattern-recall", "Pattern Recall", "Memorize highlighted cells", Category.MEMORY),
    NUMBER_RECALL("number-recall", "Number Recall", "Replay hidden digit sequences", Category.MEMORY),
    OBJECT_RECALL("object-recall", "Object Recall", "Remember which icons were shown", Category.MEMORY),

    QUICK_MATH("quick-math", "Quick Math", "Solve rapid arithmetic", Category.MATH),
    NUMBER_SERIES("number-series", "Number Series", "Complete numeric patterns", Category.MATH),
    LOGICAL_REASONING("logical-reasoning", "Logical Reasoning", "Spot the odd one out", Category.MATH),
    TIMED_MENTAL_MATH("timed-mental-math", "Timed Mental Math", "Balance speed and accuracy", Category.MATH),

    TARGET_TAP("target-tap", "Target Tap", "Tap the target among distractors", Category.ATTENTION),
    REACTION_TEST("reaction-test", "Reaction Test", "Tap the moment the color changes", Category.ATTENTION),
    STROOP_TEST("stroop-test", "Stroop Test", "Pick the ink color, not the word", Category.ATTENTION),

    SYNONYM_ANTONYM("synonym-antonym", "Synonym & Antonym", "Match related and opposite words", Category.VOCABULARY),
    WORD_MEANING("word-meaning", "Word Meaning", "Choose the correct definition", Category.VOCABULARY),
    FILL_IN_THE_BLANK("fill-in-the-blank", "Fill in the Blank", "Finish the sentence", Category.VOCABULARY),
    SPELLING_CORRECTION("spelling-correction", "Spelling Correction", "Spot the correct spelling", Category.VOCABULARY);

    companion object {
        fun fromId(id: String): GameType = entries.firstOrNull { it.id == id } ?: PATTERN_RECALL

        fun forCategory(category: Category): List<GameType> =
            entries.filter { it.category == category }
    }
}
