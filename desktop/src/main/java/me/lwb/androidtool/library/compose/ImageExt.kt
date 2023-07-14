package me.lwb.androidtool.library.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.File
import java.io.InputStream

/**
 * Created by ve3344 .
 */
fun ByteArray.decodeComposeImageBitmap(): ImageBitmap =
    Image.makeFromEncoded(this).toComposeImageBitmap()
fun loadImageBitmap(inputStream: InputStream): ImageBitmap =
    Image.makeFromEncoded(inputStream.readAllBytes()).toComposeImageBitmap()
fun loadImageBitmap(bytes: ByteArray): ImageBitmap =
    Image.makeFromEncoded(bytes).toComposeImageBitmap()

fun loadImageBitmap(file: File) = loadImageBitmap(file.readBytes())
fun loadImageBitmap(resName: String): ImageBitmap {
    val classLoader = ImageBitmap::class.java.classLoader
    val res =
        requireNotNull(classLoader.getResource(resName)) { "Load bitmap $resName fail" }
    return loadImageBitmap(res.readBytes())
}

