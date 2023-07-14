package me.lwb.androidtool.impl

import android.system.Os
import com.google.auto.service.AutoService
import me.lwb.jsonrpc.RpcMethod
import me.lwb.jsonrpc.RpcService
import me.lwb.androidtool.common.services.PingService
@AutoService(RpcService::class)
class PingServiceImpl : PingService {
    @RpcMethod
    override fun ping(): String = "OK"
    @RpcMethod
    override fun pid(): Int = Os.getpid()
}