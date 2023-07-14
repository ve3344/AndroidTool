package me.lwb.androidtool

import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.android.AndroidJsSession
import me.lwb.androidtool.utils.connectTerminal
import kotlin.concurrent.thread

/**
 * Created by ve3344 .
 */
//    public int SendAdbCommands(Context context, final byte[] fileBase64, final String ip, String localip, int bitrate, int size) {
//        this.context = context;
//        status = 1;
//        final StringBuilder command = new StringBuilder();
//        command.append(" CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / org.las2mile.scrcpy.Server ");
//        command.append(" /" + localip + " " + Long.toString(size) + " " + Long.toString(bitrate) + ";");
fun main() {
    thread {
        AndroidJsSession(LocalAdb(),null)
            .open("org.las2mile.scrcpy.Server  192.168.66.73").connectTerminal()
    }


//    val scrcpy = Scrcpy()
//    scrcpy.start("192.168.67.45",1920,1080)

}