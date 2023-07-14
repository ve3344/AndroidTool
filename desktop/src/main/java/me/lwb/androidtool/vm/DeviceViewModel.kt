package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.base.InitializeLoadFeature
import me.lwb.androidtool.config.Config
import me.lwb.androidtool.data.bean.ApkBean
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.bean.InfoConfigBean
import me.lwb.androidtool.data.repository.DeviceRepository
import java.io.File

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class DeviceViewModel : BaseViewModel(), InitializeLoadFeature {
    private val repository = DeviceRepository()

    val apkFiles: MutableStateFlow<List<ApkBean>> = MutableStateFlow(emptyList())
    val currentDeviceProp: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    val showInfoConfig: MutableStateFlow<List<InfoConfigBean>> = MutableStateFlow(emptyList())
    val devices get() = AdbManager.devices


    override fun onInitializeLoad() {
        launch {
            currentDevice.filterNotNull().collect {
                loadDeviceProp()
            }
        }

        loadDevices()
        loadInfoConfig()
    }

    fun loadInfoConfig() {
        launch {
            showInfoConfig.value = repository.loadInfoConfig(Config.INFO_CONFIG)
        }
    }

    fun loadDevices() {
        launch {
            val deviceList = repository.loadDevices()
            AdbManager.setDevices(deviceList)

        }
    }


    fun installApk(file: File) {
        launch() {
            require(file.canRead())
            require(file.extension.equals("apk", true))
            showLoadingDialog("安装中")
            val res = repository.install(file.path)
            if (res.isEmpty()) {
                showSucceedDialog("安装成功")
            } else {
                showFailDialog("安装失败", res)
            }

        }
    }

    private fun loadDeviceProp() {
        launch {
            currentDevice.value ?: return@launch
            currentDeviceProp.value = repository.loadDeviceProp()
        }
    }

    fun changeCurrent(it: DeviceBean) {
        AdbManager.changeCurrent(it)
    }

    fun addApks(files: List<File>) {
        launch {
            apkFiles.value = apkFiles.value + (files.map { ApkBean(it) })
        }

    }

    fun delete(apk: ApkBean) {
        launch {
            apkFiles.value = apkFiles.value.filterNot { it == apk }
        }
    }

}