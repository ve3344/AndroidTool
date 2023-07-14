package me.lwb.androidtool.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors

/**
 * Created by ve3344 .
 */
open class Themes {
    val Dark= darkColors(
        primary = ThemeColors.TextBackground,
        primaryVariant = ThemeColors.TextBackground,
        secondary = ThemeColors.TextBackground,
    )
    val Light= lightColors(
        primary = ThemeColors.TextBackground,
        primaryVariant = ThemeColors.TextBackground,
        secondary = ThemeColors.TextBackground,
    )
}