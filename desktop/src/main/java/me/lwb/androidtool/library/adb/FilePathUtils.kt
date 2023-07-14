package me.lwb.androidtool.library.adb

/**
 * Created by ve3344 .
 */
object FilePathUtils {

    fun getChildPath(path: String?, name: String): String {
        if (path == null) {
            if (name.startsWith("/") || name.endsWith("\\")) {
                return name
            }
            return "/$name"
        } else {
            return getPathAsDir(path) + name
        }
    }

    fun getPathAsDir(path: String): String {
        if (path.endsWith("/") || path.endsWith("\\")) {
            return path
        }
        return "$path/"
    }

    fun getPathAsFile(path: String): String {
        if (path.endsWith("/") || path.endsWith("\\")) {
            return path.slice(0 until path.lastIndex)
        }
        return path
    }
}