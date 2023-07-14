package me.lwb.androidtool.common.services

import me.lwb.jsonrpc.RpcService

interface PingService: RpcService {
    override val serviceName: String  get() = PingService::class.java.simpleName
    fun ping(): String
    fun pid(): Int
}