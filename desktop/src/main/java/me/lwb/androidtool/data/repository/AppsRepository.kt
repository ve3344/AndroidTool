package me.lwb.androidtool.data.repository

import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.withRpc
import me.lwb.androidtool.common.services.PackageService
import me.lwb.androidtool.common.services.bean.AppPackage
import me.lwb.androidtool.common.services.bean.AppPackageDetail
import me.lwb.androidtool.data.bean.AppFilterBean

/**
 * Created by ve3344 .
 */
open class AppsRepository {

    suspend fun loadApps(pageIndex:Int,pageSize:Int,appFilterData:AppFilterBean) =
        withRpc<PackageService, List<AppPackage>>() {
            it.apps(pageIndex, pageSize,appFilterData.searchKey,appFilterData.showSystem)
        }
    suspend fun appDetail(packageName:String) =
        withRpc<PackageService, AppPackageDetail>() {
            it.appDetail(packageName)
        }
    suspend fun uninstall(packageName:String){
        AdbManager.shell("pm uninstall $packageName")
    }

   suspend fun getLaunchActivity(packageName: String)  =
       withRpc<PackageService, String>() {
           it.getLaunchActivity(packageName)

    }
}