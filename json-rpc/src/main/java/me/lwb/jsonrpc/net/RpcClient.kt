/*
 * The MIT License
 *
 * Copyright 2018 Lars Ivar Hatledal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.lwb.jsonrpc.net


import kotlinx.serialization.json.JsonElement
import me.lwb.jsonrpc.RpcCall
import me.lwb.jsonrpc.RpcCallException
import me.lwb.jsonrpc.RpcCallback
import me.lwb.jsonrpc.RpcErrorCode
import me.lwb.jsonrpc.RpcParams
import me.lwb.jsonrpc.RpcProtocol
import me.lwb.jsonrpc.RpcRequest
import me.lwb.jsonrpc.RpcSerializer
import me.lwb.logger.loggerForClass
import java.io.Closeable
import java.util.UUID
import java.util.concurrent.Executors

/**
 * @author Lars Ivar Hatledal
 */
interface RpcClient :Closeable {


    fun notify(methodName: String, params: List<JsonElement>)
    fun notify(methodName: String, params: Map<String, JsonElement>)
    fun notify(methodName: String) = notify(methodName, emptyList())

    val activeRequestCount: Int

    fun request(methodName: String, params: List<JsonElement>, callback: RpcCallback): RpcCall

    fun request(methodName: String, params: Map<String, JsonElement>, callback: RpcCallback): RpcCall

    fun request(methodName: String, callback: RpcCallback): RpcCall =
        request(methodName, emptyList(), callback)

    abstract class AbstractRpcClient() : RpcClient {
        private val executor = Executors.newSingleThreadExecutor()
        private val callbacks = mutableMapOf<String, RpcCallback>()

        override val activeRequestCount: Int
            get() = callbacks.size

        override fun notify(methodName: String, params: List<JsonElement>) {
            sendNotify(methodName, RpcParams.ListParams(params))
        }


        override fun notify(methodName: String, params: Map<String, JsonElement>) {
            sendNotify(methodName, RpcParams.RpcMapParams(params))
        }

        override fun request(
            methodName: String,
            params: List<JsonElement>,
            callback: RpcCallback,
        ): RpcCall {
            return sendRequest(methodName, RpcParams.ListParams(params), callback)
        }

        override fun request(
            methodName: String,
            params: Map<String, JsonElement>,
            callback: RpcCallback,
        ): RpcCall = sendRequest(methodName, RpcParams.RpcMapParams(params), callback)


        private fun sendNotify(methodName: String, params: RpcParams) {
            val request = RpcSerializer.serializeRequest(
                RpcRequest(id = RpcProtocol.NO_ID,
                methodName = methodName,
                params = params)
            )
            try {
                writePackage(request)
            } catch (e: Throwable) {
                throw RpcCallException(
                    RpcErrorCode.CALL_WRITE_FAIL.code,
                    cause = e
                )
            }

        }


        private fun sendRequest(
            methodName: String,
            params: RpcParams,
            callback: RpcCallback,
        ): RpcCall {
            val reqId = UUID.randomUUID().toString()

            val request = RpcSerializer.serializeRequest(
                RpcRequest(id = reqId,
                methodName = methodName,
                params = params)
            )

            callbacks[reqId] = callback
            try {
                writePackage(request)
                return RpcCall {
                    callbacks.remove(reqId)
                }
            } catch (e: Throwable) {
                val callback0 = callbacks.remove(reqId) ?: return RpcCall.EMPTY
                try {
                    callback0.onException(
                        RpcCallException(
                            RpcErrorCode.CALL_WRITE_FAIL.code,
                            cause = e
                        )
                    )
                } catch (e: Throwable) {
                    LOG.w("Handle rpc fail $e")
                }
                return RpcCall.EMPTY
            }
        }

        protected abstract fun writePackage(message: String)

        protected fun notifyPackageReceived(message: String) {
            val response = RpcSerializer.parseResponse(message)
            val id = response.id
            val callback = callbacks.remove(id) ?: return
            if (response.error != null) {
                callback.onException(RpcCallException(response.error.code, response.error.message))
            } else {
                callback.onResult(response.result)
            }

        }

        override fun close() {
            callbacks.clear()
            executor.shutdown()
        }

        private companion object {
            private val LOG = loggerForClass()
        }

    }
}

fun RpcClient.blockRequest(methodName: String, params: List<JsonElement>, timeout: Long): JsonElement {
    val syncCallback = RpcCallback.SyncCallback()
    val call = request(methodName, params, syncCallback)
    try {
        return syncCallback.await(timeout)
    } catch (e: InterruptedException) {
        call.cancel()
        throw e
    }
}

