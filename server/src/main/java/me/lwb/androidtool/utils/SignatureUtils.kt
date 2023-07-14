@file:Suppress("DEPRECATION")

package me.lwb.androidtool.utils

import java.io.File
import java.io.IOException
import java.security.cert.Certificate
import java.util.jar.JarEntry
import java.util.jar.JarFile

object SignatureUtils {

    @Throws(IOException::class)
    fun getSignaturesFromApk(apkPath: String): ByteArray? {
        JarFile(File(apkPath)).use {
            val je = it.getJarEntry("AndroidManifest.xml")
            val readBuffer = ByteArray(8192)
            val certs = loadCertificates(it, je, readBuffer)
            for (c in certs) {
                return c.encoded
            }
        }
        return null
    }


    fun loadCertificates(
        jarFile: JarFile,
        je: JarEntry,
        readBuffer: ByteArray,
    ): Array<Certificate> {
        jarFile.getInputStream(je).use {
            while (it.read(readBuffer, 0, readBuffer.size) != -1) {
            }
            return je.certificates

        }

    }

}