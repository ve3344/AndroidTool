package me.lwb.androidtool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import me.lwb.androidtool.ui.widget.BottomBar

fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {
    Column {
        Box(Modifier.weight(1f))
        BottomBar()
    }
}
