package me.lwb.androidtool

import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.android.AndroidJsSession
import me.lwb.androidtool.utils.connectTerminal

/**
 * Created by ve3344 .
 */
fun main() {

    //CLASSPATH=/data/local/tmp/server-debug.apk app_process /system/bin me.lwb.androidtool.App
    AndroidJsSession(LocalAdb(),null).open("me.lwb.androidtool.Test").connectTerminal()


}

//android.app.ActivityThread.currentApplication()
//new me.lwb.androidtool.android.service.ServiceManager()
//new me.lwb.androidtool.android.service.ServiceManager().getClipboardManager()
//new me.lwb.androidtool.android.service.ServiceManager().getClipboardManager().getText()