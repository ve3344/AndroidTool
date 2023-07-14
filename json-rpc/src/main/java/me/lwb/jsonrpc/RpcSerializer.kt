@file:OptIn(ExperimentalSerializationApi::class)

package me.lwb.jsonrpc


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.serializer
import java.lang.reflect.Type

object RpcSerializer {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        this.explicitNulls = true
    }

    fun parseRequest(message: String): RpcRequest = json.decodeFromString(message)

    fun serializeRequest(request: RpcRequest): String = json.encodeToString(request)

    fun serializeResponse(result: RpcResponse): String = json.encodeToString(result)

    fun parseResponse(message: String): RpcResponse = json.decodeFromString(message)

    fun serializeMethodArg(type: Type,any: Any?): JsonElement {
        any?:return JsonNull
        return json.encodeToJsonElement(json.serializersModule.serializer(type),any)
    }
    fun parseMethodArg(type: Type,arg: JsonElement): Any? =
        json.decodeFromJsonElement(json.serializersModule.serializer(type),arg)


    fun serializeMethodReturnValue(type: Type, result: Any?): JsonElement {
        if (type == Void::class.java || type == Void::class.javaPrimitiveType) {
            return JsonNull
        }
        return json.encodeToJsonElement(json.serializersModule.serializer(type),result?:JsonNull)
    }

    fun parseMethodReturnValue(type: Type,arg: JsonElement): Any? {
        if (type == Unit::class.java) {
            return Unit
        }
        if (type == Void::class.java || type == Void::class.javaPrimitiveType) {
            return null
        }
        try {
            return json.decodeFromJsonElement(json.serializersModule.serializer(type),arg)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }



}

