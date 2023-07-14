package me.lwb.androidtool.base

import me.lwb.androidtool.data.local.LocalSetting
import me.lwb.androidtool.library.adb.AdbInstallUtils
import me.lwb.androidtool.library.platform.jvm.Jar
import me.lwb.androidtool.library.zip.ZipUtils
import me.lwb.logger.loggerForClass
import java.io.File

/**
 * Created by ve3344 .
 */
object Installer {
    private val logger = loggerForClass()


    private var init: Boolean = false
    fun init() {
        if (init) {
            return
        }
        init = true
        try {
            checkAndInstall()
        } catch (e: Throwable) {
            logger.e("Init application fail", e)
        }
    }

    private fun checkAndInstall() {
        val file = File("config")
        if (file.exists()) {
            return
        }
        onInstall()
    }

    private fun onInstall() {
        installDefaultResources()
        setupDefaultAdbPath()
    }


    private fun setupDefaultAdbPath() {

        logger.d("setupDefaultAdbPath start ")

        val path = sequenceOf("adb", "adb.exe", "bin/adb.exe").firstOrNull {
            AdbInstallUtils.isAdbPathValid(it)
        } ?: throw IllegalStateException("Can not find valid adb program!!")
        LocalSetting.adbPath = path
        logger.d("setupDefaultAdbPath done ,result=$path")

    }

    private fun installDefaultResources() {
        logger.d("installDefaultResources start")
        Jar.requireResource("/pack.zip").use {
            ZipUtils.unzip(it, "")
        }
        logger.d("installDefaultResources done")
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        setupDefaultAdbPath()
        packageDefaultResources()
    }

    fun packageDefaultResources() {
        val file = File("desktop/src/main/resources/pack.zip")
        val map = listOf("bin", "config").map { File(it).absoluteFile }

        ZipUtils.zip(map, file.outputStream())
    }
}