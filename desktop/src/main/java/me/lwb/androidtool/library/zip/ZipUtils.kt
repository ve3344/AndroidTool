package me.lwb.androidtool.library.zip

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.attribute.FileTime
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Created by ve3344 .
 */
object ZipUtils {
    fun zip(files: List<File>, os: OutputStream) {
        ZipOutputStream(os).use { zos ->
            files.forEach {
                zipItem(it, zos, it.name)
            }
        }
    }

    private fun zipItem(file: File, zos: ZipOutputStream, name: String) {
        if (file.isFile) {
            zos.putNextEntry(ZipEntry(name).apply {
                lastModifiedTime = FileTime.fromMillis(file.lastModified())
                size = file.length()
            })

            file.inputStream().use {
                it.copyTo(zos)
                zos.closeEntry()
            }
        } else {
            val children = file.listFiles() ?: emptyArray()
            if (children.isEmpty()) {
                zos.putNextEntry(ZipEntry("$name/"))
                zos.closeEntry()
            } else {
                for (child in children) {
                    zipItem(child, zos, "$name/${child.name}")
                }
            }
        }
    }

    private fun ZipInputStream.entries() = sequence<ZipEntry> {
        var e: ZipEntry? = nextEntry
        while (e != null) {
            yield(e)
            e = nextEntry
        }
    }

    fun unzip(ins: InputStream, dir: String) {
        val destFile = File(dir)
        if (!destFile.exists()) {
            destFile.mkdirs()
        }
        val zis = ZipInputStream(ins)
        for (entry in zis.entries()) {
            val name = entry.name
            val outPath = (dir + name).replace("\\", "/")

            val endIndex = name.lastIndexOf('/')
            if (endIndex != -1) {
                val file = File(outPath.substring(0, outPath.lastIndexOf("/")))
                if (!file.exists()) {
                    file.mkdirs()
                }
            }

            val outFile = File(outPath)
            if (outFile.isDirectory) {
                continue
            }
            FileOutputStream(outPath).use {
                zis.copyTo(it)
            }
        }


    }

}