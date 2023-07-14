package me.lwb.androidtool.android

import android.annotation.SuppressLint
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Looper
import me.lwb.androidtool.utils.*

/**
 * Created by ve3344 .
 */
@SuppressLint("PrivateApi", "StaticFieldLeak")
object FakeApp {
    const val PACKAGE_NAME = "com.android.shell"
    const val USER_ID = 0

    @JvmStatic
    lateinit var application: Application
        private set
    @JvmStatic
    var userId: Int = 0
        private set
    private var isInit = false

    @JvmStatic
    fun init() {
        if (isInit) {
            return
        }
        isInit = true

        Environment.initForCurrentUser()
        kotlin.runCatching { ActivityThread.initializeMainlineModules() }
        Looper.prepare()


        val activityThread = ActivityThread.new()
        activityThread.attach(true, 2)

        bindApplication(activityThread)


        application = ActivityThread.currentApplication()
        userId = USER_ID
    }

    private fun bindApplication(activityThread: ActivityThread) {
        val bindData = ActivityThread.AppBindData(ActivityThread.AppBindData.new())
        bindData.appInfo = ApplicationInfo().apply {
            packageName = PACKAGE_NAME
        }
        activityThread.mBoundApplication = bindData.delegateObject

    }

}


class ActivityThread(override val delegateObject: Any) : IObjectReflector {
    companion object : IClassReflector by classReflector("android.app.ActivityThread") {
        val new by construct<ActivityThread>()
        val initializeMainlineModules by staticMethod<Any>()


        val currentApplication by staticMethod<Application>()

        var sCurrentActivityThread by staticField<Any>()

    }

    val getSystemContext by method<ContextImpl>()
    val getSystemUiContext by method<Context>()
    var mBoundApplication by field<Any>()
    var mSystemThread by field<Boolean>()

    var mInstrumentation: Instrumentation by field()
    var mInitialApplication: Application by field()

    fun attach(system: Boolean, startSeq: Long) {
        sCurrentActivityThread = this
        mSystemThread = system
        try {
            val instrumentation = Instrumentation()
            kotlin.runCatching { InstrumentationWrapper(instrumentation).basicInit(this) }
            mInstrumentation = instrumentation


            val systemContext = getSystemContext()
            val systemPackageInfo = systemContext.mPackageInfo
            systemPackageInfo.mPackageName = FakeApp.PACKAGE_NAME

            val context = try {
                ContextImpl.createAppContext(this, systemPackageInfo, FakeApp.PACKAGE_NAME)
            } catch (e: Exception) {
                ContextImpl.createAppContext2(this, systemPackageInfo)
            }



            val packageInfo = context.mPackageInfo
            val initialApplication =
                packageInfo.makeApplicationCompat(true, null)
            initialApplication.onCreate()

            mInitialApplication = initialApplication

        } catch (e: Exception) {
            throw RuntimeException(
                "Unable to instantiate Application():$e", e)
        }
    }

    class InstrumentationWrapper(override val delegateObject: Any) : IObjectReflector {
        val basicInit by method<Any>(ActivityThread)
    }

    class AppBindData(override val delegateObject: Any) : IObjectReflector {

        companion object :
            IClassReflector by classReflector("android.app.ActivityThread\$AppBindData") {
            val new by construct<Any>()
        }

        var appInfo: ApplicationInfo by field()
    }
}

object Environment : IClassReflector by classReflector("android.os.Environment") {
    val initForCurrentUser by staticMethod<Context>()

}

class ContextImpl(override val delegateObject: Any) : IObjectReflector {
    var mBasePackageName by field<String>()
    var mOpPackageName by field<String>()
    var mPackageInfo by field<LoadedApk>()


    companion object : IClassReflector by classReflector("android.app.ContextImpl") {

        val createAppContext by staticMethod<ContextImpl>(ActivityThread, LoadedApk, String::class.java)
        val createAppContext2 by staticMethod<ContextImpl>(ActivityThread, LoadedApk).overrideOf(::createAppContext)



    }


}

class LoadedApk(override val delegateObject: Any) : IObjectReflector {
    var mPackageName by field<String>()
    var mApplicationInfo by field<ApplicationInfo>()
    var mAppDir by field<String>()
    var mResDir by field<String>()
    var mDataDir by field<String>()
    var mLibDir by field<String>()
    var mApplication by field<Application>()
    var mResources by field<Resources>()
    var mClassLoader by field<ClassLoader>()


    val makeApplicationInner by method<Application>(Boolean::class.java,
        Instrumentation::class.java)
    val makeApplication by method<Application>(Boolean::class.java, Instrumentation::class.java)

    fun makeApplicationCompat(
        forceDefaultAppClass: Boolean,
        instrumentation: Instrumentation?,
    ): Application {
        try {
            return makeApplicationInner(forceDefaultAppClass, instrumentation, true)
        } catch (e: Exception) {
            return makeApplication(forceDefaultAppClass, instrumentation)
        }
    }
    companion object : IClassReflector by classReflector("android.app.LoadedApk")


}

class ConfigurationController {
    companion object :
        IClassReflector by classReflector("android.app.ConfigurationController") {
        val new by construct<Any>("android.app.ActivityThreadInternal")
    }
}

class UserHandle() {
    companion object : IClassReflector by classReflector("android.os.UserHandle") {
        val myUserId by staticMethod<Int>()


    }
}

class ApplicationPackageManager {
    companion object : IClassReflector by classReflector("android.app.ApplicationPackageManager") {
        val new by construct<PackageManager>(ContextImpl,
            "android.content.pm.IPackageManager")
    }
}