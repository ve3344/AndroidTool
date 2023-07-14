package me.lwb.androidtool.library.platform.jvm

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object Libs {

    fun extractLibrary(resourcePath: String): String {
        val resourceFile = File(resourcePath)

        val prefix = resourceFile.nameWithoutExtension
        val suffix = resourceFile.extension
        val tmp = File.createTempFile(prefix, ".$suffix").absoluteFile
        if (!tmp.exists()) {
            throw IOException("Create temp file fail")
        }
        val data = Libs::class.java.getResourceAsStream(resourcePath)
        Files.copy(
            data,
            tmp.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
        return tmp.toPath().toString()
    }

    private var loadedLibraries = HashSet<String>()

    @Synchronized
    fun loadLibrary(resourcePath: String) {
        if (loadedLibraries.contains(resourcePath)) {
            return
        }
        val extractLibrary = extractLibrary(resourcePath)
        System.load(extractLibrary)
        loadedLibraries.add(resourcePath)
    }
}