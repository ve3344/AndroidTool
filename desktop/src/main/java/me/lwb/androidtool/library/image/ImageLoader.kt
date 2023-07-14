package me.lwb.androidtool.library.image

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.lwb.androidtool.App
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.withRpc
import me.lwb.androidtool.common.utils.decodeBase64
import me.lwb.androidtool.common.services.PackageService
import me.lwb.androidtool.library.compose.loadImageBitmap

/**
 * Created by ve3344 .
 */
object ImageLoader {
    class PainterHolder(val painter: Painter?)

    private val iconCache: LruCache<String, PainterHolder> =
        LruCache(50)

    private val mutex = Mutex()

    init {

        App.appScope.launch {
            AdbManager.currentDevice.collect {
                iconCache.evictAll()
            }
        }
    }

    suspend fun load(packageName: String): Painter? {
        return mutex.withLock {
            val holder = iconCache.get(packageName)?: PainterHolder(loadAppIcon(packageName)).also {
                iconCache.put(packageName,it)
            }
            holder.painter
        }


    }

    private suspend fun loadAppIcon(packageName: String): Painter? =
        withRpc<PackageService, Painter?>() {
            val iconBase64 = it.getIconBase64(packageName)
            if (iconBase64.isEmpty()) {
                return@withRpc null
            }
            BitmapPainter(loadImageBitmap(iconBase64.decodeBase64()))
        }


}