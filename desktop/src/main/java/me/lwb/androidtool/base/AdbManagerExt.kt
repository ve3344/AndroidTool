package me.lwb.androidtool.base

import me.lwb.jsonrpc.RpcService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lwb.androidtool.library.adb.IAdb
import java.lang.reflect.UndeclaredThrowableException

/**
 * Created by ve3344 .
 */
suspend inline fun <reified S : RpcService, reified T> withRpc(
    crossinline action: (S) -> T,
): T {
    return withContext(Dispatchers.IO) {
        try {
            action(AdbManager.adbRpcServiceFactory.getService(AdbManager.requireDevice(), S::class.java))
        }catch (e: UndeclaredThrowableException){
            throw e.cause?:e
        }
    }
}


suspend inline fun <T> withAdb(crossinline action: (IAdb) -> T): T {
    return withContext(Dispatchers.IO) { action(AdbManager.adb) }
}
