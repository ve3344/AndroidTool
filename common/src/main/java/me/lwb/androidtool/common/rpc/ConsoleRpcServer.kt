package me.lwb.androidtool.common.rpc

import me.lwb.jsonrpc.RpcHandler
import me.lwb.jsonrpc.net.RpcServer
import me.lwb.logger.loggerForClass
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by ve3344 .
 */
open class ConsoleRpcServer(
    private val handler: RpcHandler,
    requestSteam: InputStream,
    responseSteam: OutputStream,
) : RpcServer {
    private val logger = loggerForClass()
    private val reader = BufferedReader(InputStreamReader(requestSteam))
    private val writer = BufferedWriter(OutputStreamWriter(responseSteam))

    private var running = true

    override fun start() {
        while (running) {
            try {
                loopOne(reader, writer)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun loopOne(
        reader: BufferedReader,
        writer: BufferedWriter,
    ) {
        val request = reader.readStringPackage()
        if (request.isEmpty()) {
            return
        }
        try {
            handler.handle(request)?.let {
//                logger.d("W $it")
                writer.writeStringPackage(it)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    override fun stop() {
        running = false
    }
}

