package me.lwb.androidtool.common.rpc

import me.lwb.jsonrpc.RpcService
import me.lwb.jsonrpc.net.RpcClient
import java.lang.reflect.Proxy

/**
 * Created by ve3344 .
 */
@Suppress("UNCHECKED_CAST")
fun <T : RpcService> RpcClient.proxy(clazz: Class<T>): T =
    Proxy.newProxyInstance(
        clazz.classLoader, arrayOf(clazz),
        RemoteInvocationHandler(clazz.simpleName, this)
    ) as T

inline fun <reified T : RpcService> RpcClient.proxy() = proxy(T::class.java)