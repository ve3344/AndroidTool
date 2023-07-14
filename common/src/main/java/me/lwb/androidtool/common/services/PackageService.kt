package me.lwb.androidtool.common.services

import me.lwb.jsonrpc.RpcService
import me.lwb.androidtool.common.services.bean.AppPackage
import me.lwb.androidtool.common.services.bean.AppPackageDetail

interface PackageService : RpcService {
    override val serviceName: String get() = PackageService::class.java.simpleName
    fun apps(pageIndex: Int, pageSize: Int, searchKey: String, showSystem: Boolean): List<AppPackage>
    fun appDetail(packageName: String): AppPackageDetail
    fun getLaunchActivity(packageName: String): String
    fun getIconBase64(packageName: String): String
}