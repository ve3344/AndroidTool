package me.lwb.androidtool.library.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File

/**
 * Created by ve3344 .
 */
class JsonSetting<T>(private val path: String, private val serializer: KSerializer<T>) {
    fun loadSetting(): T {
        return Json.decodeFromString(serializer, File(path).readText())
    }

    fun saveSetting(value: T) {
        val dst = File(path)
        val tmp = File("$path.tmp")
        val text = Json.encodeToString(serializer, value)
        tmp.writeText(text)

        try {
            if(dst.exists()){
                check(dst.delete()) { "Can not save setting !!" }
            }
            check(tmp.renameTo(dst)) { "Can not save setting !" }
        } finally {
            tmp.delete()
        }
    }
}

inline fun <reified T> JsonSetting(path: String) = JsonSetting(path, serializer<T>())