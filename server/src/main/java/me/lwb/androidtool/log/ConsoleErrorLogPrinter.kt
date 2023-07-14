package me.lwb.androidtool.log

import me.lwb.logger.Logger
import java.io.PrintStream

class ConsoleErrorLogPrinter(lineBreak: Boolean = true) : Logger.LogPrinter {

    val appendLog: (PrintStream, String) -> Unit =
        if (lineBreak) PrintStream::println else PrintStream::print

    override fun log(
        level: Logger.LogLevel,
        tag: String,
        messageAny: Any?,
        throwable: Throwable?
    ) {
        val message = messageAny ?: return
        val target = System.err
        appendLog(target, "$tag:$message")
    }
}