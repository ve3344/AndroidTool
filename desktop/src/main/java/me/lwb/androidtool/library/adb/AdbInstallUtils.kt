package me.lwb.androidtool.library.adb

import me.lwb.androidtool.library.shell.Shell
import me.lwb.androidtool.library.shell.isSucceed
import me.lwb.androidtool.library.shell.waitResult
import me.lwb.logger.loggerForClass

/**
 * Created by ve3344 .
 */
object AdbInstallUtils {

    private val logger = loggerForClass()

    fun isAdbPathValid(executable: String)= getAdbPath(executable).isNotEmpty()
    fun getAdbPath(executable: String): String {
        val res = Shell.exec("$executable version").waitResult(true)
        if (!res.isSucceed) {
            return ""
        }
        return res.output
            .lineSequence()
            .filter { it.startsWith("Installed as ") }
            .firstOrNull()
            ?.removePrefix("Installed as ")
            ?.trim()
            ?: ""
    }


}