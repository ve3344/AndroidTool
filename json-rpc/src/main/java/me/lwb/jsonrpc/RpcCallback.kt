package me.lwb.jsonrpc

import kotlinx.serialization.json.JsonElement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by ve3344 .
 */
interface RpcCallback {
    fun onResult(result: JsonElement)
    fun onException(error: RpcCallException)


    class SyncCallback : RpcCallback {
        private val latch = CountDownLatch(1)
        private var res: Any? = null

        override fun onResult(result: JsonElement) {
            res = result
            latch.countDown()
        }

        override fun onException(error: RpcCallException) {
            res = error
            latch.countDown()
        }

        fun await(timeout: Long): JsonElement {
            if (timeout<0){
                latch.await()
            }else{
                if (!latch.await(timeout, TimeUnit.MILLISECONDS)) {
                    throw RpcCallException(RpcErrorCode.CALL_TIMEOUT.code)
                }
            }

            when (val resLocal = res) {
                is JsonElement -> return res as JsonElement
                is Exception -> throw resLocal
                else -> throw RpcCallException(RpcErrorCode.CALL_UNKNOWN_ERROR.code)
            }
        }
    }
}