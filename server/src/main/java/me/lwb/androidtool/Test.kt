package me.lwb.androidtool

import android.app.ActivityManager
import android.content.Context
import me.lwb.androidtool.android.FakeApp
import me.lwb.androidtool.utils.IClassReflector
import me.lwb.androidtool.utils.classReflector
import me.lwb.androidtool.utils.staticMethod
import java.io.FileDescriptor
import java.io.PrintWriter

/**
 * Created by ve3344 .
 */
object Test {
    private val mQuitLock = Object()

    object ActivityManagerDelegate:
        IClassReflector by classReflector("android.app.ActivityManager") {

    val dumpPackageStateStatic by staticMethod<Void>(FileDescriptor::class, String::class)
        //PrintWriter pw, FileDescriptor fd, String name, String[] args
    val dumpService by staticMethod<Void>(PrintWriter::class, FileDescriptor::class,String::class,Array::class)
    }


    @JvmStatic
    fun main(args: Array<String>) {



        try {
            println("a")
            FakeApp.init()
            println("b")
            val am = FakeApp.application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            println(am.getRecentTasks(10000,0).size)

        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}