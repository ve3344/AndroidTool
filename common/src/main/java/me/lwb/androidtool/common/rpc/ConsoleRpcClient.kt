package me.lwb.androidtool.common.rpc

import me.lwb.jsonrpc.net.RpcClient
import me.lwb.logger.loggerForClass
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.concurrent.thread
/**
 * Created by ve3344 .
 */
open class ConsoleRpcClient(
    requestSteam: OutputStream,
    responseSteam: InputStream,
) : RpcClient.AbstractRpcClient() {
    private val logger = loggerForClass()
    private val writer = BufferedWriter(OutputStreamWriter(requestSteam))
    private val reader = BufferedReader(InputStreamReader(responseSteam))

    private var running = true

    init {

        thread {
            while (running) {
                val res = reader.readStringPackage()
                if (res.isEmpty()) {
                    continue
                }
                notifyPackageReceived(res)
            }
        }
    }

    override fun writePackage(message: String) {
        writer.writeStringPackage(message)

    }


    override fun close() {
        super.close()
        running = false
    }


}

