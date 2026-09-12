package com.fpculcasi.carezze.ui.theme

import androidx.compose.ui.graphics.Color

val PersonColorPalette: List<Color> = listOf(
    Color(0xFF4DB6AC), // Teal
    Color(0xFF81C784), // Green
    Color(0xFFFFB74D), // Amber
    Color(0xFFE57373), // Red
    Color(0xFF64B5F6), // Blue
    Color(0xFFBA68C8), // Purple
    Color(0xFFF06292), // Pink
    Color(0xFFA1887F), // Brown
)

fun personColor(colorIndex: Int): Color =
    PersonColorPalette[colorIndex.coerceIn(0, PersonColorPalette.lastIndex)]
