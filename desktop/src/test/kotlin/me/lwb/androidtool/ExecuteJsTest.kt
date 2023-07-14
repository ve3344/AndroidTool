package me.lwb.androidtool

import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.adb.getError
import me.lwb.androidtool.library.adb.getOutput
import me.lwb.androidtool.library.adb.writeCommand
import me.lwb.androidtool.library.android.AndroidJsSession
import java.io.File

/**
 * Created by ve3344 .
 */
fun AndroidJsSession.execute(text: String): Pair<String, String> {
    val tempFile = File.createTempFile("cachejs", ".js")
    tempFile.deleteOnExit()
    tempFile.writeText(text)

    adb.push(deviceBean, tempFile.absolutePath, "/data/local/tmp/cachejs.js")
    open("me.lwb.androidtool.Shell /data/local/tmp/cachejs.js").use {
        it.writeCommand("exit")
        it.waitResult()
        return it.getError() to it.getOutput()
    }
}
fun main() {

    //CLASSPATH=/data/local/tmp/server-debug.apk app_process /system/bin me.lwb.androidtool.App

    val res= AndroidJsSession(LocalAdb(),null).execute("""
         pm=android.content.pm.IPackageManager${'$'}Stub.asInterface(android.os.ServiceManager.getService("package"))
         new me.lwb.androidtool.android.service.ServiceManager()
    """.trimIndent())

    println(res.first)
    System.err.println(res.second)

}

//android.app.ActivityThread.currentApplication()
//new me.lwb.androidtool.android.service.ServiceManager()
//new me.lwb.androidtool.android.service.ServiceManager().getClipboardManager()
//new me.lwb.androidtool.android.service.ServiceManager().getClipboardManager().getText()