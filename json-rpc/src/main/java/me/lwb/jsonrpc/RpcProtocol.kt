package me.lwb.jsonrpc

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * Created by ve3344 .
 */
object RpcProtocol {
    const val NO_ID = ""

    const val JSON_RPC_VERSION = "2.0"
    const val JSON_RPC_IDENTIFIER = "jsonrpc"

    const val METHOD_KEY = "method"
    const val PARAMS_KEY = "params"
    const val ID_KEY = "id"

    const val DATA_KEY = "data"
    const val MESSAGE_KEY = "message"
    const val CODE_KEY = "code"
    const val ERROR_KEY = "error"
    const val RESULT_KEY = "result"

}
@Serializable
sealed class RpcParams {
    abstract val paramCount: Int
    @Serializable
    object NoParams : RpcParams() {
        override val paramCount: Int get() = 0
    }
    @Serializable
    data class ListParams(val value: List<JsonElement>) : RpcParams() {
        override val paramCount: Int get() = value.size
    }
    @Serializable
    data class RpcMapParams(val value: Map<String, JsonElement>) : RpcParams() {
        override val paramCount: Int get() = value.size
    }
}
typealias RpcId = String

@Serializable
data class RpcRequest(
    @SerialName(RpcProtocol.ID_KEY)
    @Contextual
    val id: RpcId,
    @SerialName(RpcProtocol.JSON_RPC_IDENTIFIER)
    val version: String = RpcProtocol.JSON_RPC_VERSION,
    @SerialName(RpcProtocol.METHOD_KEY)
    val methodName: String?,
    @SerialName(RpcProtocol.PARAMS_KEY)
    val params: RpcParams,
) {
    val isNotification
        get() = id == RpcProtocol.NO_ID

}

@Serializable
data class RpcError(
    @SerialName(RpcProtocol.CODE_KEY)
    val code: Int,
    @SerialName(RpcProtocol.MESSAGE_KEY)
    val message: String?,
    @SerialName(RpcProtocol.DATA_KEY)
    val data: JsonElement = JsonNull,
)

@Serializable
data class RpcResponse constructor(
    @SerialName(RpcProtocol.ID_KEY)
    val id: RpcId,
    @SerialName(RpcProtocol.JSON_RPC_IDENTIFIER)
    val version: String = RpcProtocol.JSON_RPC_VERSION,
    @SerialName(RpcProtocol.ERROR_KEY)
    val error: RpcError? = null,
    @SerialName(RpcProtocol.RESULT_KEY)
    val result: JsonElement = JsonNull,
) {
    companion object Factory {
        fun result(id: RpcId, result: JsonElement) = RpcResponse(id = id, result = result)
        fun error(id: RpcId, code: Int, message: String? = null, data: JsonElement = JsonNull) =
            RpcResponse(id = id, error = RpcError(code, message, data))
    }
}



