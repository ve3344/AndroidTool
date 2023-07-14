package me.lwb.androidtool

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import me.lwb.androidtool.ui.page.ApkPage
import me.lwb.androidtool.vm.DeviceViewModel

fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {
    ApkPage(DeviceViewModel())
}