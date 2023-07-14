package me.lwb.androidtool.library.android

import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.library.adb.IAdb
import me.lwb.androidtool.library.adb.IAdbStream
import me.lwb.androidtool.library.adb.shell
import me.lwb.androidtool.library.adb.writeCommand

/**
 * Created by ve3344 .
 */
open class AndroidJsSession(
    val adb: IAdb,
    val deviceBean: DeviceBean?,
) {

    companion object {
        private var isInstall = false

        const val SERVER_FILE = "server\\build\\outputs\\apk\\debug\\server-debug.apk"
    }


    fun killAll(program: String) {
        adb.shell("ps -ef | grep $program | awk '{print \$2}' | xargs kill -9", deviceBean)
    }

    fun install(file: String = SERVER_FILE): AndroidJsSession {
        if (isInstall) {
            return this
        }
        adb.push(deviceBean,
            file,
            "/data/local/tmp/")
        isInstall = true
        return this
    }

    fun open(program: String): IAdbStream {
        install()
        return adb.openShell(null).also {
            it.writeCommand("CLASSPATH=/data/local/tmp/server-debug.apk app_process /system/bin $program")
        }
    }


}