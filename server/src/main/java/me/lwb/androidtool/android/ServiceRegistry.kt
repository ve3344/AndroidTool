package me.lwb.androidtool.android

import android.annotation.SuppressLint
import android.os.IBinder
import android.os.IInterface
import me.lwb.androidtool.utils.IClassReflector
import me.lwb.androidtool.utils.classReflector
import me.lwb.androidtool.utils.staticMethod

/**
 * Created by ve3344 .
 */
@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
object ServiceRegistry {

    object ServiceManager:IClassReflector by classReflector("android.os.ServiceManager"){
        val getService by staticMethod<IBinder>(String::class.java)
        val checkService by staticMethod<IBinder>(String::class.java)
    }
    object InputManager:IClassReflector by classReflector("android.hardware.input.InputManager"){
        val getInstance by staticMethod<Any>()
    }
    object ActivityManagerNative:IClassReflector by classReflector("android.app.ActivityManagerNative"){
        val getDefault by staticMethod<Any>()
    }
    private fun getService(service: String, type: String): IInterface {
        return try {
            val binder = ServiceManager.getService(service)
            val asInterfaceMethod =
                Class.forName("$type\$Stub").getMethod("asInterface", IBinder::class.java)
            asInterfaceMethod.invoke(null, binder) as IInterface
        } catch (e: Exception) {
            throw RuntimeException("Get service fail $service",e)
        }
    }

    val packageManager by lazy { getService("package", "android.content.pm.IPackageManager") }
    val windowManager by lazy { getService("window", "android.view.IWindowManager") }
    val displayManager by lazy { getService("display", "android.hardware.display.IDisplayManager") }

    val inputManager by lazy {
        InputManager.getInstance()
    }
    val powerManager by lazy { getService("power", "android.os.IPowerManager") }
    val statusBarManager by lazy {
        getService("statusbar",
            "com.android.internal.statusbar.IStatusBarService")
    }


    val clipboard by lazy { getService("clipboard", "android.content.IClipboard") }
    val activityManager by lazy {
        ActivityManagerNative.getDefault()
    }


}