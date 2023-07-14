package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.base.pagerOf
import me.lwb.androidtool.common.services.bean.AppPackage
import me.lwb.androidtool.common.services.bean.AppPackageDetail
import me.lwb.androidtool.data.bean.AppFilterBean
import me.lwb.androidtool.data.local.LocalSetting
import me.lwb.androidtool.data.repository.AppsRepository
import me.lwb.androidtool.library.loadmore.LoadMoreStatus
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.logger.loggerForClass
import java.io.File

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class AppsViewModel() : BaseViewModel() {

    val logger = loggerForClass()

    private val repository = AppsRepository()

    val appPager = pagerOf<AppPackage>()

    val appFilterData = MutableStateFlow(AppFilterBean("", false))

    val appDetail: MutableStateFlow<AppPackageDetail?> = MutableStateFlow(null)

    init {
       launch {
           appPager.source.statusFlow.filterIsInstance<LoadMoreStatus.Fail>().collect{

            it.throwable.printStackTrace()
           }
       }

    }
    fun loadAppList() {
        appPager.suspendReload {
            println("loadApps (${it.pageIndex} ${it.pageSize})")
            repository.loadApps(it.pageIndex, it.pageSize, appFilterData.value).also {
                println("loadApps = " + it.map { it.packageName })
            }
        }
    }

    fun clearAppInfo() {
        appDetail.value = null
    }

    fun loadAppInfo(packageName: String) {
        launch {
            appDetail.value = repository.appDetail(packageName)
        }
    }

    fun uninstall(packageName: String) {
        launch {
            appDetail.value = null
            showLoadingDialog("卸载中")
            repository.uninstall(packageName)
            showSucceedDialog("卸载成功 $packageName")
        }
    }

    fun launchApp(packageName: String) {
        launch {
            val ac = repository.getLaunchActivity(packageName)
            AdbManager.awaitShell("am start -n $packageName/$ac")

            GlobalUi.showToast("启动成功")
        }
    }

    fun stopApp(packageName: String) {
        launch {
            AdbManager.awaitShell("am force-stop $packageName")
            GlobalUi.showToast("停止成功")
        }
    }

    fun export(detail: AppPackage) {
        val out = "${detail.label}-v${detail.versionName}.apk"
        export(detail.packageName, out)
    }

    fun export(detail: AppPackageDetail) {
        val out = "${detail.label}-v${detail.versionName}.apk"
        export(detail.packageName, out)
    }

    fun export(packageName: String, outName: String) {
        launch {
            val result = AdbManager.awaitShell("pm path $packageName")
            val path = result.removePrefix("package:").trim()
            val exportAppDir = LocalSetting.exportAppDir
            val local = if (exportAppDir.isNotEmpty()) {
                File(exportAppDir, outName).absolutePath
            } else {
                File(outName).absolutePath
            }
            showLoadingDialog("导出App","正在导出到 $local")

            AdbManager.pull(path, local)
            showSucceedDialog("导出成功", "保存位置：$local")
        }
    }

    fun updateFilterData(filterKey: String, showSystem: Boolean) {
        launch {
            appFilterData.value = AppFilterBean(filterKey, showSystem)
        }
    }


}