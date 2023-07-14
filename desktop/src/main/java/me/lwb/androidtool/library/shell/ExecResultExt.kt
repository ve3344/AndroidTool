package me.lwb.androidtool.library.shell


val ExecResult.isSucceed
    get() = status == 0

val ExecResult.isTimeout
    get() = this == ExecResult.EXEC_TIMEOUT


inline fun ExecResult.onSucceed(block: (output: String) -> Unit): ExecResult {
    if (isSucceed) {
        block(output)
    }
    return this
}


inline fun ExecResult.onFail(block: (code: Int, error: String) -> Unit): ExecResult {
    if (!isSucceed && !isTimeout) {
        block(status, error)
    }
    return this
}


inline fun ExecResult.onTimeout(block: () -> Unit): ExecResult {
    if (isTimeout) {
        block()
    }
    return this
}

