package me.lwb.androidtool

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.lwb.androidtool.common.services.PackageService
import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.platform.jvm.ShutdownHooks
import me.lwb.androidtool.library.rpc.AdbRpcServiceFactory
import kotlin.system.measureTimeMillis

/**
 * Created by ve3344 .
 */
fun main() {
    val adb: LocalAdb = LocalAdb()
    val serviceFactory = AdbRpcServiceFactory(adb)

    val time = measureTimeMillis {
        runBlocking {
            val deviceBean = adb.devices().first()
            val service: PackageService by serviceFactory.serviceOf { deviceBean }

            val apps = service.apps(1, 20, "", true)
            println("App size= ${apps.size}")

            for (app in apps) {

                launch(Dispatchers.IO) {
                    val icon :String
                    val time=measureTimeMillis {
                        icon=service.getIconBase64(app.packageName)

                    }
                    println("App[${app.packageName}] : ${icon.length} cost :$time ms")
                }
            }
        }
    }
    ShutdownHooks.add{
        serviceFactory.close()
    }

    println("Cost = $time ms")

}