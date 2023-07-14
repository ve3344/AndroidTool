package me.lwb.androidtool

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.base.InitializeLoadFeature
import me.lwb.androidtool.base.Installer
import me.lwb.androidtool.base.ViewModelStore
import me.lwb.androidtool.config.Config
import me.lwb.androidtool.data.local.LocalSettingManager
import me.lwb.androidtool.library.compose.loadImageBitmap
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.MainWidget
import me.lwb.androidtool.ui.widget.WindowBar
import java.util.ServiceLoader
import kotlin.system.exitProcess

private lateinit var composeWindow: ComposeWindow
val LocalWindow = staticCompositionLocalOf { composeWindow }


object App {

    val appScope = CoroutineScope(SupervisorJob())

    @JvmStatic
    fun main(args: Array<String>) {
        val viewModels = ServiceLoader.load(BaseViewModel::class.java)
        viewModels.forEach{
            ViewModelStore.register(it)
        }

        val icon = BitmapPainter(loadImageBitmap("painter/adb.png"))
        singleWindowApplication(
            title = "AndroidTool",
            state = WindowState(width = Config.WINDOW_WIDTH, height = Config.WINDOW_HEIGHT),
            icon = icon,
            transparent = true,
            undecorated = true
        ) {
            composeWindow = this.window

            MaterialTheme(
                colors = MaterialTheme.colors.copy(
                    ThemeColors.ThemeMain,
                    ThemeColors.ThemeMain,
                    ThemeColors.ThemeMain,
                    ThemeColors.ThemeMain,
                )
            ) {
                WindowBar("", Painters.adb, onMinimizedRequest = {
                    composeWindow.isMinimized = true
                }, onChangeAlwaysOnTopRequest = {
                    composeWindow.isAlwaysOnTop =it
                }, onCloseRequest = {
                    window.isVisible = false
                    exitProcess(0)
                }) {

                    MainWidget()
                    GlobalUi.compose()

                }
            }

            SideEffect {
                LocalSettingManager.load()
                Installer.init()
                ViewModelStore.map.values.filterIsInstance<InitializeLoadFeature>()
                    .forEach { it.onInitializeLoad() }
            }
        }
    }

}


