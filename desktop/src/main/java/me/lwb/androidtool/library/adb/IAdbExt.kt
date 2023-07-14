package me.lwb.androidtool.library.adb

import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.library.shell.Shell
import me.lwb.logger.Logger
import java.nio.charset.Charset

/**
 * Created by ve3344 .
 */
fun IAdbStream.getOutput(): String {
    return inputStream.bufferedReader().readText()
}
fun IAdbStream.getError(): String {
    return errorStream.bufferedReader().readText()
}

fun <T> IAdbStream.getLines(ignoreLine:Int=1,parse: (String) -> T?): List<T> {
    return use {
        inputStream
            .bufferedReader()
            .lineSequence()
            .drop(ignoreLine)
            .filter { it.isNotEmpty() }
            .distinct()
            .mapNotNull { parse(it) }
            .toList()
    }
}

fun IAdbStream.writeCommand(command: String, charset: Charset = Charset.defaultCharset()) {
    if (command.isEmpty()) {
        return
    }
    outputStream.write(command.toByteArray(charset))
    outputStream.write(Shell.COMMAND_SUFFIX.toByteArray(charset))
    outputStream.flush()
}


fun IAdb.shell(command: String, deviceBean: DeviceBean?): ShellResult {
    return openShell(deviceBean).use {
        Logger.d("shell[$command]")
        it.writeCommand(command)
        it.writeCommand("exit")
        ShellResult(it.getOutput(),it.getError(),it.waitResult(0))
    }
}