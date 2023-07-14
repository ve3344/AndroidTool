package me.lwb.androidtool.library.shell

import java.io.File
import java.nio.charset.Charset

/**
 * @author: luo
 * @create: 2020-12-15 15:05
 */
@Suppress("UNUSED")
object Shell {
    val COMMAND_SUFFIX: String = System.lineSeparator();

    const val COMMAND_EXIT = "exit"

    val PROGRAM_SU by lazy {
        if (Platform.currentPlatform === Platform.Linux) "su" else "cmd"
    }

    val PROGRAM_SH by lazy {
        if (Platform.currentPlatform === Platform.Linux) "sh" else "cmd"
    }
    val DEFAULT_CHARSET by lazy {
        if (Platform.currentPlatform === Platform.Windows) Charset.forName("gbk") else Charset.defaultCharset()
    }

    fun sh(command: String, needResult: Boolean = true, timeout: Long = 0): ExecResult {
        return shell(PROGRAM_SH, command, needResult, timeout)
    }

    fun su(command: String, needResult: Boolean = true, timeout: Long = 0): ExecResult {
        return shell(PROGRAM_SU, command, needResult, timeout)
    }

    fun checkSuAccess(): Boolean = runCatching { shell("su", "id").isSucceed }
        .getOrDefault(false)

    @Suppress("DEPRECATION")
    fun exec(
        program: String,
        env: Array<String>? = null,
        file: File? = null
    ): Process = Runtime.getRuntime().exec(program, env, file)

    fun getExecutableSuCommand(): String? {
        sh("which su").onSucceed {
            val file = File(it)
            if (file.exists() && file.canExecute()) {
                return file.absolutePath
            }
        }

        return arrayOf(
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        ).firstOrNull {
            val file = File(it)
            file.exists() && file.canExecute()
        }
    }

    fun shell(
        program: String,
        command: String,
        needResult: Boolean = true,
        timeout: Long = 0,
        charset: Charset = DEFAULT_CHARSET
    ): ExecResult {
        return exec(program).use {
            it.writeCommands(command, COMMAND_EXIT, charset = charset)
            it.waitResult(needResult, timeout, charset)
        }

    }
}