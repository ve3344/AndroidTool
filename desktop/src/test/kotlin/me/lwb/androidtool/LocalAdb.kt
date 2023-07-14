package me.lwb.androidtool

import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.adb.shell

/**
 * Created by ve3344 .
 */
fun main() {
    val adb = LocalAdb()
    adb.devices().forEach {
        println(adb.shell("getprop", it))
    }
}