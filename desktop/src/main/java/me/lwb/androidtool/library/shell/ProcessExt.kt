package me.lwb.androidtool.library.shell

import kotlinx.coroutines.flow.asFlow
import java.io.Closeable
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by ve3344 .
 */
/**
 * 生成use方法
 */
inline fun <R> Process.use(block: (Process) -> R) = Closeable { close() }.use { block(this) }
fun Process.writeCommands(vararg commands: String, charset: Charset = Charset.defaultCharset()) {
    commands.forEach { writeCommand(it, charset) }
}

fun Process.writeCommand(command: String, charset: Charset = Charset.defaultCharset()) {
    if (command.isEmpty()) {
        return
    }
    outputStream.write(command.toByteArray(charset))
    outputStream.write(Shell.COMMAND_SUFFIX.toByteArray(charset))
    outputStream.flush()
}

fun Process.grabOutput(charset: Charset = Charset.defaultCharset()) =
    inputStream.bufferedReader(charset).readText()

fun Process.grabError(charset: Charset = Charset.defaultCharset()) =
    errorStream.bufferedReader(charset).readText()

fun Process.outputFlow() = inputStream.bufferedReader().lineSequence().asFlow()
fun Process.errorFlow() = errorStream.bufferedReader().lineSequence().asFlow()

fun Process.close() {
    kotlin.runCatching {
        errorStream.close()
        inputStream.close()
        outputStream.close()
        destroy()
    }
}

fun Process.waitResult(
    needResult: Boolean,
    timeout: Long = 0,
    charset: Charset = Charset.defaultCharset()
): ExecResult {
    try {
        val code = if (timeout <= 0) {
            waitFor()
        } else {
            if (!waitFor(timeout, TimeUnit.MILLISECONDS)) {
                return ExecResult.EXEC_TIMEOUT
            }
            exitValue()
        }

        if (!needResult) {
            return ExecResult(code)
        }
        return ExecResult(code, grabOutput(charset), grabError(charset))
    } finally {
        close()
    }
}
