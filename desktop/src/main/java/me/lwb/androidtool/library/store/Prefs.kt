package me.lwb.androidtool.library.store

import java.util.prefs.Preferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Prefs {

    fun clear(prefName: String = PREF_DEFAULT){
        sp(prefName).clear()
    }
    const val PREF_DEFAULT = "prefs_default"
    private inline fun <T> delegate(
        key: String? = null,
        defaultValue: T,
        crossinline getter: Preferences.(String, T) -> T,
        crossinline setter: Preferences.(String, T) -> Unit,
        prefName: String = PREF_DEFAULT,
    ): ReadWriteProperty<Any?, T> =
        object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T =
                sp(prefName).getter(key ?: property.name, defaultValue)!!

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                sp(prefName).setter(key ?: property.name, value)
            }
        }

    fun sp(prefName: String) =
        Preferences.userRoot().node(prefName)

    fun int(
        key: String? = null, defValue: Int = 0, prefName: String = PREF_DEFAULT
    ): ReadWriteProperty<Any?, Int> {
        return delegate(
            key,
            defValue,
            Preferences::getInt,
            Preferences::putInt,
            prefName
        )
    }

    fun long(
        key: String? = null, defValue: Long = 0, prefName: String = PREF_DEFAULT
    ): ReadWriteProperty<Any?, Long> {
        return delegate(
            key,
            defValue,
            Preferences::getLong,
            Preferences::putLong,
            prefName
        )
    }

    fun float(
        key: String? = null, defValue: Float = 0f, prefName: String = PREF_DEFAULT,
    ): ReadWriteProperty<Any?, Float> {
        return delegate(
            key,
            defValue,
            Preferences::getFloat,
            Preferences::putFloat,
            prefName
        )
    }
    fun double(
        key: String? = null, defValue: Double = 0.0, prefName: String = PREF_DEFAULT,
    ): ReadWriteProperty<Any?, Double> {
        return delegate(
            key,
            defValue,
            Preferences::getDouble,
            Preferences::putDouble,
            prefName
        )
    }

    fun boolean(
        key: String? = null, defValue: Boolean = false, prefName: String = PREF_DEFAULT,
    ): ReadWriteProperty<Any?, Boolean> {
        return delegate(
            key,
            defValue,
            Preferences::getBoolean,
            Preferences::putBoolean,
            prefName
        )
    }


    fun string(
        key: String? = null, defValue: String = "",
        prefName: String = PREF_DEFAULT,
    ): ReadWriteProperty<Any?, String> {
        return delegate(
            key,
            defValue,
            Preferences::get,
            Preferences::put,
            prefName
        )
    }
}