@file:OptIn(ExperimentalFoundationApi::class)

package me.lwb.androidtool.library.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton

/**
 * Created by ve3344 .
 */
@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.onMouseLeftClick(crossinline onClick: () -> Unit) =
    onClick(true, matcher = PointerMatcher.mouse(PointerButton.Primary)) {
        onClick()
    }
@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.onMouseRightClick(crossinline onClick: () -> Unit) =
    onClick(true, matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
        onClick()
    }