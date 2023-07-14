package me.lwb.androidtool.common

import me.lwb.jsonrpc.RpcMethod
import me.lwb.androidtool.common.services.PingService

class PingServiceImpl: PingService {
    @RpcMethod
    override fun ping(): String ="OK"
    override fun pid(): Int {
        TODO("Not yet implemented")
    }
}