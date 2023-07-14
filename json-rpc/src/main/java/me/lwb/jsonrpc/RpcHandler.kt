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

package me.lwb.jsonrpc

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import me.lwb.jsonrpc.RpcProtocol.JSON_RPC_VERSION
import java.lang.reflect.Method

/**
 * @author Lars Ivar Hatledal
 */
@Suppress("NewApi")
class RpcHandler private constructor(
    services: Map<String, RpcService>,
) {
    private val serviceRegistry = RpcServiceRegistry(services)

    constructor(vararg services: RpcService) : this(services.associateBy { it.serviceName })
    constructor(services: List<RpcService>) : this(services.associateBy { it.serviceName })


    fun handle(json: String): String? {
        return handleForResponse(json)?.let {
            RpcSerializer.serializeResponse(it)
        }
    }

    private fun handleForResponse(json: String): RpcResponse? {
        val request = try {
            RpcSerializer.parseRequest(json)
        } catch (e: Throwable) {
            return RpcResponse.error(
                RpcProtocol.NO_ID,
                RpcErrorCode.PARSE_ERROR.code,
                "Exception encountered while parsing json string: $json"
            )
        }
        return try {
            val result = handleRequest(request)
            if (request.isNotification) {
                return null
            }
            RpcResponse.result(request.id, result)
        } catch (e: Throwable) {
            val exception = RpcHandleException.wrap(e)
            RpcResponse.error(
                request.id,
                exception.code,
                (exception.message ?: e.toString()) + exception.stackTraceToString()
            )
        }

    }

    private fun handleRequest(req: RpcRequest): JsonElement {
        if (req.version != JSON_RPC_VERSION) {
            throw RpcHandleException(
                RpcErrorCode.INVALID_REQUEST.code,
                "Wrong or invalid jsonrpc version: ${req.version}"
            )
        }
        val serviceMethodName = req.methodName
            ?: throw RpcHandleException(RpcErrorCode.INVALID_REQUEST.code, "No method specified!")

        if (req.isNotification) {
            return JsonNull
        }
        val method = serviceRegistry.getHandleMethod(serviceMethodName, req.params)
        val result = method.invoke(req.params)

        return RpcSerializer.serializeMethodReturnValue(method.method.genericReturnType, result)
    }

    class HandleMethod(val service: RpcService, val method: Method) {
        operator fun invoke(params: RpcParams): Any? {
            val args = params.toMethodArgs(method)
            return if (args.isEmpty()) {
                method.invoke(service)
            } else {
                method.invoke(service, *args.toTypedArray())
            }
        }

        @Suppress("NewApi")
        private fun RpcParams.toMethodArgs(method: Method): List<Any?> {
            val args = when (this) {
                RpcParams.NoParams -> emptyList()
                is RpcParams.ListParams -> value
                is RpcParams.RpcMapParams -> method.parameters.map {
                    requireNotNull(value[it.name]) { "Require arg '${it.name}'" }
                }
            }
            return args.zip(method.parameterTypes) { arg, type ->
                RpcSerializer.parseMethodArg(type, arg)
            }
        }
    }


}
