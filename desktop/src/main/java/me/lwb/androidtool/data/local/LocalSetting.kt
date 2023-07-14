package me.lwb.androidtool.data.local

import me.lwb.androidtool.library.store.JsonSetting
import java.io.FileNotFoundException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by ve3344 .
 */

object LocalSetting {
    var exportAppDir: String by LocalSettingManager.delegate("exportAppDir")
    var adbPath: String by LocalSettingManager.delegate("adbPath", "adb.exe")

}


object LocalSettingManager {

    private val setting: MutableMap<String, String> = hashMapOf()
    private val localSetting = JsonSetting<Map<String, String>>("config/setting.json")
    @Synchronized
    operator fun get(key: String) = setting[key]
    @Synchronized
    operator fun set(key: String, value: String) {
        setting[key] = value
    }

    @Synchronized
    fun load() {
        try {
            val values = localSetting.loadSetting()
            setting.clear()
            setting.putAll(values)
        } catch (_: FileNotFoundException) {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @Synchronized
    fun save() {
        localSetting.saveSetting(setting)
    }

    fun delegate(key: String, default: String = "") = object : ReadWriteProperty<Any, String> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return setting[key] ?: default
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            setting[key] = value
        }
    }

}

