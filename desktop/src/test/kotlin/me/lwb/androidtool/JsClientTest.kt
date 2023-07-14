package me.lwb.androidtool

import kotlinx.coroutines.runBlocking
import me.lwb.androidtool.common.rpc.ConsoleRpcClient
import me.lwb.androidtool.common.utils.decodeBase64
import me.lwb.androidtool.common.rpc.proxy
import me.lwb.androidtool.common.services.PackageService
import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.android.AndroidJsSession
import java.io.File
import kotlin.concurrent.thread

/**
 * Created by ve3344 .
 */
fun main() {

    //packageManager.getAllPackages()
    runBlocking {
        val adb = LocalAdb()
        AndroidJsSession(adb,null).open("me.lwb.androidtool.RpcServer").use {
            thread {
                try {
                    it.errorStream.transferTo(System.err)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val client = ConsoleRpcClient(it.outputStream, it.inputStream)

            val pingService = client.proxy(PackageService::class.java)
//            println("Response=${pingService.apps()}")

            val icon= pingService.getIconBase64("com.alicloud.databox")
            println("Response=${icon}")

            File("a.png").writeBytes(icon.decodeBase64())

        }



    }
}