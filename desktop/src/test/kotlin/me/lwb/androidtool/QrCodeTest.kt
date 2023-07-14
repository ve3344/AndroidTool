package me.lwb.androidtool

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import me.lwb.androidtool.library.adb.LocalAdb

/**
 * Created by ve3344 .
 */
fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {

    val adb = LocalAdb()
//    val wirelessPair = ScanPairDeviceUtils(adb)




}