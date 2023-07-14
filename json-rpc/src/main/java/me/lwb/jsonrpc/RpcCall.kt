package me.lwb.jsonrpc

/**
 * Created by ve3344 .
 */
fun interface RpcCall {
    fun cancel()

    companion object {
        val EMPTY = RpcCall {}
    }
}