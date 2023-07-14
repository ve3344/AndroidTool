package me.lwb.jsonrpc

import me.lwb.jsonrpc.RpcParams.NoParams.paramCount
import java.lang.reflect.Method

/**
 * Created by ve3344 .
 */
@Suppress("NewApi")
open class RpcServiceRegistry(private val services: Map<String, RpcService>) {
    private val loadedMethods = hashMapOf<RpcService, List<Method>>()
    private val cacheMethods = hashMapOf<String, RpcHandler.HandleMethod>()


    private fun getHandleMethod(service: RpcService, name: String, numParams: Int): RpcHandler.HandleMethod {
        val methods = loadedMethods.getOrPut(service) {
            service.javaClass
                .declaredMethods
                .filter { it.isAnnotationPresent(RpcMethod::class.java) }
        }
        val method = methods
            .firstOrNull { it.name == name && it.parameterCount == numParams }
            ?: throw RpcHandleException(
                RpcErrorCode.METHOD_NOT_FOUND.code,
                "No such method '$name' in service '${service.serviceName}' that takes $paramCount params"
            )
        return RpcHandler.HandleMethod(service, method)
    }


    fun getHandleMethod(serviceMethodName: String, params: RpcParams): RpcHandler.HandleMethod {
        val cacheKey = "${serviceMethodName}#${params.paramCount}"
        return cacheMethods.getOrPut(cacheKey) { findHandleMethod(serviceMethodName, params) }
    }

    private fun findHandleMethod(serviceMethodName: String, params: RpcParams): RpcHandler.HandleMethod {
        val split = serviceMethodName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
        val service: RpcService
        val methodName: String
        when (split.size) {
            2 -> {
                val serviceName = split[0]
                service = services[serviceName]
                    ?: throw RpcHandleException(
                        RpcErrorCode.METHOD_NOT_FOUND.code,
                        "No such registered service '$serviceName'",
                    )

                methodName = split[1]
            }
            1 -> {
                service = services.values.toList().first()
                methodName = serviceMethodName
            }
            else -> {
                throw RpcHandleException(
                    RpcErrorCode.INVALID_REQUEST.code,
                    "Multiple services defined and method does not use '.' to separate service and method!"
                )
            }
        }
        return getHandleMethod(service, methodName, params.paramCount)
    }






}