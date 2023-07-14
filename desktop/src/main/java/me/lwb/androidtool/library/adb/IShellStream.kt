package me.lwb.androidtool.library.adb

/**
 * Created by ve3344 .
 */
interface IShellStream :AutoCloseable{
    fun writeCommand(command: String)
    fun getOutput():String
    fun getError():String
    fun getStatusCode():Int
}

data class ShellResult(val output:String,val error:String,val statusCode:Int)