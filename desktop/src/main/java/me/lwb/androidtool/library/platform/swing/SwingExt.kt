package me.lwb.androidtool.library.platform.swing

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileFilter
import kotlin.coroutines.resume

object SwingUtils {
    private var init = false
    suspend fun chooseFile(config: JFileChooser.() -> Unit) =
        suspendCancellableCoroutine<File?> { c ->
            setWindowsLookAndFeels()

            val chooser = JFileChooser().apply(config)
            c.invokeOnCancellation {
                chooser.cancelSelection()
            }
            when (chooser.showOpenDialog(null)) {
                JFileChooser.APPROVE_OPTION -> c.resume(chooser.selectedFile)
                else -> c.resume(null)
            }

        }

    fun setWindowsLookAndFeels() {
        if (init) {
            return
        }
        init = true
        runCatching {
            UIManager.getInstalledLookAndFeels().firstOrNull { "Windows" == it.name }?.let {
                UIManager.setLookAndFeel(it.className)
            }
        }
    }

    @Throws(Exception::class)
    fun browse(url: String) {
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        }
    }

    fun copy(img: ImageBitmap) {
//        Toolkit.getDefaultToolkit().getSystemClipboard().setContents()

    }


    fun fileFilter(desc: String, predicate: (File) -> Boolean)= object : FileFilter() {
        override fun accept(f: File): Boolean = predicate(f)

        override fun getDescription(): String = desc

    }
}

