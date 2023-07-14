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

import java.lang.reflect.InvocationTargetException

/**
 * @author Lars Ivar Hatledal
 */
//-32700	解析错误	服务器接收到无效的JSON；服务器解析JSON文本发生错误。
//-32600	无效的请求	发送的JSON不是一个有效的请求。
//-32601	方法未找到	方法不存在或不可见。
//-36602	无效的参数	无效的方法参数。
//-36603	内部错误	JSON-RPC内部错误。
//-32000到-32099	服务器端错误	保留给具体实现服务器端错误。
enum class RpcErrorCode(val code: Int) {
    PARSE_ERROR(-32700),
    METHOD_NOT_FOUND(-32601),
    INVALID_REQUEST(-32600),
    INVALID_PARAMS(-32602),
    INTERNAL_ERROR(-32603),
    SERVER_ERROR(-32000),

    CALL_UNKNOWN_ERROR(-40000),
    CALL_WRITE_FAIL(-40001),
    CALL_TIMEOUT(-40002),
    ;


}

class RpcCallException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

class RpcHandleException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    companion object {
        fun wrap(e: Throwable): RpcHandleException {
            return when (e) {
                is RpcHandleException -> e
                is InvocationTargetException -> RpcHandleException(
                    RpcErrorCode.SERVER_ERROR.code,
                    cause = e.targetException
                )
                is IllegalArgumentException -> RpcHandleException(
                    RpcErrorCode.INVALID_PARAMS.code,
                    cause = e
                )
                else -> RpcHandleException(RpcErrorCode.INTERNAL_ERROR.code, cause = e)
            }
        }
    }
}


