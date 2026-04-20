package com.mindforge.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.mindforge.app.domain.model.Category

// Premium Palette - Muted but Rich
val BackgroundLight = Color(0xFFFBF9F2)
val BackgroundDark = Color(0xFF0F1113)
val CardBaseLight = Color(0xFFFFFFFF)
val CardBaseDark = Color(0xFF1E1E1E)

// Strong Accent Colors
val MemoryAccent = Color(0xFF6FAF8F)
val MathAccent = Color(0xFF6C8EBF)
val AttentionAccent = Color(0xFFD9A441)
val VocabularyAccent = Color(0xFF9A6FB0)

// Soft Gradient Backgrounds
val MemoryGradStart = Color(0xFFE8F3EC)
val MemoryGradEnd = Color(0xFFDDEBE4)
val MathGradStart = Color(0xFFE7EEF7)
val MathGradEnd = Color(0xFFDCE6F2)
val AttentionGradStart = Color(0xFFF3EBDD)
val AttentionGradEnd = Color(0xFFEADFCB)
val VocabularyGradStart = Color(0xFFEFE7F3)
val VocabularyGradEnd = Color(0xFFE5DAEC)

// New Daily Challenge Design Colors
val QuestionGradStart = Color(0xFFDCEFE3)
val QuestionGradEnd = Color(0xFFCFE5D8)
val AccentGreen = Color(0xFF4CAF50)

val FlameColor = Color(0xFFFF954D)
val WoodColor = Color(0xFFC0A080)

val BorderSoft = Color(0x1A000000)

fun categoryColor(category: Category): Color = when (category) {
    Category.MEMORY -> MemoryAccent
    Category.MATH -> MathAccent
    Category.ATTENTION -> AttentionAccent
    Category.VOCABULARY -> VocabularyAccent
}

fun categoryGradient(category: Category): List<Color> = when (category) {
    Category.MEMORY -> listOf(MemoryGradStart, MemoryGradEnd)
    Category.MATH -> listOf(MathGradStart, MathGradEnd)
    Category.ATTENTION -> listOf(AttentionGradStart, AttentionGradEnd)
    Category.VOCABULARY -> listOf(VocabularyGradStart, VocabularyGradEnd)
}
