package me.lwb.androidtool.js

import com.faendir.rhino_android.RhinoAndroidHelper
import me.lwb.androidtool.android.ServiceRegistry
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.io.Reader

/**
 * Created by ve3344 .
 */
class JsHelper : ScriptableObject(), AutoCloseable {
    override fun getClassName(): String = "JsHelper"
    private val ctx: Context = kotlin.runCatching { RhinoAndroidHelper(CACHE_DIR).enterContext() }.getOrElse {
        Context.enter()
    }

    init {
        ctx.initStandardObjects(this)

        try {
            initAndroid()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    private fun initAndroid() {
        defineProperty("packageManager",
            ServiceRegistry.packageManager,
            DONTENUM)
        defineProperty("windowManager",
            ServiceRegistry.windowManager,
            DONTENUM)
        defineProperty("displayManager",
            ServiceRegistry.displayManager,
            DONTENUM)
    }

    @Throws(Exception::class)
    fun evaluate(reader: Reader) = reader.use {
        ctx.evaluateReader(this, it, "empty", 1, null)
    }

    @Throws(Exception::class)
    fun evaluate(script: String) = ctx.evaluateString(this, script, "empty", 1, null)


    override fun close() {
        Context.exit()
    }

    companion object {
        val CACHE_DIR = File("/data/local/tmp/classes")
        val instance by lazy { JsHelper() }
    }
}