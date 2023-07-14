package me.lwb.androidtool.android.service

import android.annotation.SuppressLint
import android.os.Build
import android.os.IInterface
import me.lwb.androidtool.utils.IObjectReflector
import me.lwb.androidtool.utils.method
@SuppressLint("ObsoleteSdkInt")
class PowerManagerLike(manager: IInterface) {
    private val powerManager=IPowerManager(manager)
    fun isScreenOn(): Boolean =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) powerManager.isInteractive() else powerManager.isScreenOn()
    class IPowerManager(override val delegateObject: Any) : IObjectReflector {
        val isInteractive by method<Boolean>()
        val isScreenOn by method<Boolean>()
    }
}