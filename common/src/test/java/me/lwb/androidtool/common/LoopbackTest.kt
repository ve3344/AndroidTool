package me.lwb.androidtool.common

import me.lwb.jsonrpc.RpcHandler
import me.lwb.jsonrpc.net.RpcServer
import me.lwb.androidtool.common.rpc.ConsoleRpcClient
import me.lwb.androidtool.common.rpc.ConsoleRpcServer
import me.lwb.androidtool.common.rpc.proxy
import me.lwb.androidtool.common.services.PingService
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.concurrent.thread


class PipePair {
    val ins = PipedInputStream()
    val ous = PipedOutputStream(ins)
}

fun main() {
    val requestPipe = PipePair()
    val responsePipe = PipePair()
    val handler = RpcHandler(PingServiceImpl())
    thread {
        val server: RpcServer = ConsoleRpcServer(handler, requestPipe.ins, responsePipe.ous)
        server.start()
    }

    Thread.sleep(100)
    val client = ConsoleRpcClient(requestPipe.ous, responsePipe.ins)

    val pingService = client.proxy(PingService::class.java)
    try {
        println("Response=${pingService.pid()}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}