package me.lwb.androidtool.common.rpc

import me.lwb.jsonrpc.RpcSerializer
import me.lwb.jsonrpc.RpcService
import me.lwb.jsonrpc.net.RpcClient
import me.lwb.jsonrpc.net.blockRequest
import me.lwb.logger.loggerForClass
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

internal class RemoteInvocationHandler(private val prefix: String, private val client: RpcClient) :
    InvocationHandler {

    val logger = loggerForClass()

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        proxy as RpcService
        if (method.declaringClass == Object::class.java) {
            if (args == null) {
                return method.invoke(this)
            }
            return method.invoke(this, *args)
        }
        val argsList =
            args?.mapIndexed { index, any ->
                RpcSerializer.serializeMethodArg(method.genericParameterTypes[index], any)
            } ?: emptyList()


        val response = client.blockRequest("$prefix.${method.name}", argsList, 20000)
        return RpcSerializer.parseMethodReturnValue(method.genericReturnType, response)

    }

}