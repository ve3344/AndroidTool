package me.lwb.androidtool.library.shell

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by ve3344 .
 */
class AwaitShellBuilder {
    internal var onOutListener: ((String) -> Unit)? = null
    internal var onErrListener: ((String) -> Unit)? = null
    fun onOut(block: (String) -> Unit) {
        onOutListener = block
    }

    fun onErr(block: (String) -> Unit) {
        onErrListener = block
    }
}

suspend fun awaitShell(
    build: AwaitShellBuilder.() -> String,
): Int =
    withContext(Dispatchers.IO) {
        val builder = AwaitShellBuilder()
        val cmd = builder.let(build).trimIndent().replace("\n", " ")

        val process = Runtime.getRuntime().exec(cmd)
        builder.onOutListener?.let {
            launch {
                process.inputStream.bufferedReader()
                    .lineSequence()
                    .asFlow()
                    .collect(it)
            }
        }
        builder.onErrListener?.let {
            launch {
                process.errorStream.bufferedReader()
                    .lineSequence()
                    .asFlow()
                    .collect(it)
            }
        }

        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            throw CancellationException()
        }
    }

