package me.lwb.androidtool.utils

import me.lwb.androidtool.library.adb.IAdbStream
import me.lwb.androidtool.library.adb.writeCommand
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by ve3344 .
 */
fun IAdbStream.connectTerminal() {
    val it = this
    thread {
        it.inputStream.bufferedReader().lineSequence().forEach { println(it) }
    }
    thread {
        it.errorStream.bufferedReader().lineSequence().forEach { System.err.println(it) }
    }

    val scanner = Scanner(System.`in`)
    while (scanner.hasNext()) {
        val next = scanner.next()
        it.writeCommand(next)
    }

}