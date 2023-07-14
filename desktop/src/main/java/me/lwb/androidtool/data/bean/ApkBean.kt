package me.lwb.androidtool.data.bean

import net.dongliu.apk.parser.ApkFile
import java.io.File

/**
 * Created by ve3344 .
 */
open class ApkBean(val file: File) {
    val apkInfo: ApkFile = ApkFile(file)

    var apkSign: ByteArray? = null

    init {
        apkSign = kotlin.runCatching {
            apkInfo.apkV2Singers.first().certificateMetas.first().data
        }.getOrNull() ?: kotlin.runCatching {
            apkInfo.apkSingers.first().certificateMetas.first().data
        }.getOrNull()

    }
}