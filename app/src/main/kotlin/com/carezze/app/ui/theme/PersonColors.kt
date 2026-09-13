package com.fpculcasi.carezze.ui.theme

import androidx.compose.ui.graphics.Color

val PersonColorPalette: List<Color> =
    listOf(
        // Teal
        Color(0xFF4DB6AC),
        // Green
        Color(0xFF81C784),
        // Amber
        Color(0xFFFFB74D),
        // Red
        Color(0xFFE57373),
        // Blue
        Color(0xFF64B5F6),
        // Purple
        Color(0xFFBA68C8),
        // Pink
        Color(0xFFF06292),
        // Brown
        Color(0xFFA1887F),
    )

fun personColor(colorIndex: Int): Color = PersonColorPalette[colorIndex.coerceIn(0, PersonColorPalette.lastIndex)]
