package me.lwb.androidtool.vm

import androidx.compose.ui.graphics.ImageBitmap
import com.google.auto.service.AutoService
import kotlinx.coroutines.flow.MutableStateFlow
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.base.InitializeLoadFeature
import me.lwb.androidtool.config.Config
import me.lwb.androidtool.data.bean.ControlConfigBean
import me.lwb.androidtool.data.repository.ControlRepository
import me.lwb.androidtool.library.adb.ShellResult
import me.lwb.androidtool.library.compose.loadImageBitmap
import me.lwb.androidtool.ui.core.GlobalUi
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class ControlViewModel: BaseViewModel(),InitializeLoadFeature {
    private val repository= ControlRepository()
    val controlConfig: MutableStateFlow<ControlConfigBean.Group?> = MutableStateFlow(null)
    val screenshot: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    fun screenshot() {
        launch {
            val time= measureTimeMillis {
                repository.shell("screencap -p /data/local/tmp/screenshot.png" )
                File(".cache").mkdirs()
                repository.pull("/data/local/tmp/screenshot.png", ".cache/screenshot.png")
                screenshot.value = loadImageBitmap(File(".cache/screenshot.png"))
            }
            println("time =$time")
        }
    }

    override fun onInitializeLoad() {
        loadControlConfig()
    }

    fun clearScreenshot() {
        launch {
            screenshot.value = null
        }
    }
    fun loadControlConfig(path: String = Config.CONTROL_CONFIG) {
        launch {
            if (File(path).exists()) {
                val loadCustomControlConfig = repository.loadControlConfig(path)
                controlConfig.value = loadCustomControlConfig
            }

        }
    }
    fun shell(cmd: String) {
        launch {
            val output: ShellResult = repository.shell(cmd )
            println(output)
            GlobalUi.showToast(if (output.statusCode == 0) output.output else output.error)
        }

    }


}