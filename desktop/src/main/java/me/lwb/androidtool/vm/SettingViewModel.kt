package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.data.local.LocalSetting
import me.lwb.androidtool.data.local.LocalSettingManager
import me.lwb.androidtool.data.local.MutableSettingStateFlow
import me.lwb.androidtool.data.local.asSettingStateFlow
import me.lwb.androidtool.ui.core.GlobalUi
import kotlin.reflect.KMutableProperty0

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class SettingViewModel : BaseViewModel() {
    private val setting = LocalSetting

    private val settingValues = mutableListOf<MutableSettingStateFlow>()
    val adbPath = setting::adbPath.toSettingStateFlow("ADB路径")
    val exportAppDir = setting::exportAppDir.toSettingStateFlow("APK导出目录")

    fun loadSetting() {
        launch {
            settingValues.forEach { it.load() }
        }
    }

    fun saveSetting() {
        launch {
            AdbManager.changeAdbExecutable(adbPath.value.value)

            settingValues.forEach { it.save() }
            withContext(Dispatchers.IO){
                LocalSettingManager.save()
            }
            loadSetting()
            GlobalUi.showToast("保存成功")
        }
    }

    fun updateAdbPath(value: String) = adbPath.update(value)

    fun updateExportAppDir(value: String) = exportAppDir.update(value)


    private fun KMutableProperty0<String>.toSettingStateFlow(
        title: String,
        initValue: String = "",
    ) =asSettingStateFlow(title, initValue).also { settingValues.add(it) }
}