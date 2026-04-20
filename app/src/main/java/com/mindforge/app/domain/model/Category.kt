package com.mindforge.app.domain.model

enum class Category(
    val id: String,
    val title: String,
    val subtitle: String
) {
    MEMORY("memory", "Memory", "Recall patterns, objects, and sequences"),
    MATH("math", "Math", "Arithmetic, series, and logic"),
    ATTENTION("attention", "Attention", "Reaction, focus, and distraction control"),
    VOCABULARY("vocabulary", "Vocabulary", "Meanings, spelling, and word play");

    companion object {
        fun fromId(id: String): Category = entries.firstOrNull { it.id == id } ?: MEMORY
    }
}
