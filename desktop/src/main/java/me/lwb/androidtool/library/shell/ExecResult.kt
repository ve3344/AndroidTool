package me.lwb.androidtool.library.shell

/**
 * @description: ExecResult
 * @author: luo
 * @create: 2019-11-05 00:54
 */
data class ExecResult(
    val status: Int,
    val output: String = "",
    val error: String = ""
) {
    companion object {
        val EXEC_TIMEOUT = ExecResult(-1,"Exec timeout","Exec timeout")
    }
}