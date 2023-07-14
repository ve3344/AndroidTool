package me.lwb.androidtool.impl

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import com.google.auto.service.AutoService
import me.lwb.jsonrpc.RpcMethod
import me.lwb.jsonrpc.RpcService
import me.lwb.androidtool.android.FakeApp
import me.lwb.androidtool.common.utils.encodeBase64
import me.lwb.androidtool.common.services.PackageService
import me.lwb.androidtool.common.services.bean.AppPackage
import me.lwb.androidtool.common.services.bean.AppPackageDetail
import me.lwb.androidtool.common.utils.md5
import me.lwb.androidtool.common.utils.sha1
import me.lwb.androidtool.common.utils.sha256
import me.lwb.androidtool.utils.page
import java.io.ByteArrayOutputStream
import java.io.File

@AutoService(RpcService::class)
class PackageServiceImpl : PackageService {
    private val packageManager: PackageManager by lazy { FakeApp.application.packageManager }

    @RpcMethod
    fun running() {
    }

    @RpcMethod
    override fun apps(
        pageIndex: Int,
        pageSize: Int,
        searchKey: String,
        showSystem: Boolean
    ): List<AppPackage> {

        return packageManager.getInstalledPackages(0)
            .asSequence()
            .filterNot { isAppFiltered(it, searchKey, showSystem) }
            .toList()
            .page(pageIndex, pageSize)
            .map {
                AppPackage(
                    it.packageName,
                    it.applicationInfo.loadLabel(packageManager).toString(),
                    it.versionName ?: ""
                )
            }


    }

    private fun isAppFiltered(it: PackageInfo, searchKey: String, showSystem: Boolean): Boolean {
        val isFilterByShowSystem =
            !showSystem && it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        if (isFilterByShowSystem) {
            return true
        }

        val label = it.applicationInfo.loadLabel(packageManager).toString()
        val isFilterByKey =
            searchKey.isNotEmpty()
                    && !label.contains(searchKey)
                    && !it.packageName.contains(searchKey)

        if (isFilterByKey) {
            return true
        }
        return isFilterByKey
    }


    @RpcMethod
    override fun appDetail(packageName: String): AppPackageDetail {
        val it = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        val info = it.applicationInfo
        val sign = it.signatures?.firstOrNull()?.toByteArray()
        return AppPackageDetail(
            it.packageName,
            it.applicationInfo.loadLabel(packageManager).toString(),
            it.versionName ?: "",
            it.versionCode.toLong(),
            it.firstInstallTime,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                info.minSdkVersion
            } else 0,
            info.targetSdkVersion,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                info.compileSdkVersion
            } else 0,
            sign?.md5() ?: "",
            sign?.sha1() ?: "",
            sign?.sha256() ?: "",
            it.applicationInfo.sourceDir.toString(),
            "${(File(it.applicationInfo.sourceDir).length() / 1024)} kb"
        )

    }

    @RpcMethod
    override fun getLaunchActivity(packageName: String): String {
        return packageManager.getLaunchIntentForPackage(packageName)?.component?.className ?: ""
    }

    @RpcMethod
    override fun getIconBase64(packageName: String): String {
        return getIconBytes(packageName).encodeBase64()
    }

    fun getIconBytes(packageName: String): ByteArray {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val drawable = packageManager.getDrawable(
            packageInfo.applicationInfo.packageName,
            packageInfo.applicationInfo.icon,
            packageInfo.applicationInfo
        ) ?: return byteArrayOf()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        Canvas(bitmap).apply {
            drawable.draw(this)
        }
        val bao = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao)
        bitmap.recycle()
        return bao.toByteArray()
    }


}