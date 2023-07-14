package me.lwb.androidtool.library.adb

import java.io.InputStream
import java.io.OutputStream

/**
 * Created by ve3344 .
 */
interface IAdbStream : AutoCloseable {
    val inputStream: InputStream
    val errorStream: InputStream
    val outputStream: OutputStream

    fun waitResult(timeout:Long=0):Int

    companion object{
        const val EXEC_TIMEOUT=-1
    }
}