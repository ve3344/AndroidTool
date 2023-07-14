package me.lwb.androidtool.library.platform.jvm

import java.lang.management.ManagementFactory


object Jar {
    val JAR_PATH: String by lazy {
        Jar::class.java.protectionDomain.codeSource.location.path
    }
    val ENTRANCE_PATH = System.getProperty("user.dir")!!

    fun openResource(resourcePath: String) = Jar::class.java.getResourceAsStream(resourcePath)
    fun requireResource(resourcePath: String) = requireNotNull(Jar::class.java.getResourceAsStream(resourcePath)){
        "Require resource $resourcePath but not exist"
    }
    fun getResource(resourcePath: String) = Jar::class.java.getResource(resourcePath)

    fun loadLibrary(resourcePath: String) {
        Libs.loadLibrary(resourcePath)
    }

    fun safeLoadLibrary(resourcePath: String) :Boolean{
        return kotlin.runCatching { Libs.loadLibrary(resourcePath) }.isSuccess
    }

    val PID: Int by lazy {
        val runtime = ManagementFactory.getRuntimeMXBean()
        val name = runtime.name //"pid@hostname"
        try {
            name.substring(0, name.indexOf('@')).toInt()
        } catch (e: Exception) {
            -1
        }
    }
}