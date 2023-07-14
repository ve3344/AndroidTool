package me.lwb.androidtool

import me.lwb.jsonrpc.RpcHandler
import me.lwb.jsonrpc.RpcService
import me.lwb.jsonrpc.net.RpcServer
import me.lwb.androidtool.android.FakeApp
import me.lwb.androidtool.common.rpc.ConsoleRpcServer
import me.lwb.androidtool.js.JsHelper
import me.lwb.androidtool.log.ConsoleErrorLogPrinter
import me.lwb.logger.Logger
import java.util.ServiceLoader

class RpcServer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Logger.apply {
                tag = "RpcServer"
                logPrinter = ConsoleErrorLogPrinter()
            }
            try {

                Logger.e("Start")

                startReal()
            } catch (e: Throwable) {
                e.printStackTrace()
                Logger.e("Start fail $e")
            }
        }

        private fun startReal() {
            FakeApp.init()
            JsHelper.instance

            val services=ServiceLoader.load(RpcService::class.java)
            val handler = RpcHandler(services.toList())
            val server: RpcServer = ConsoleRpcServer(handler, System.`in`, System.out)
            server.start()
        }
    }
}