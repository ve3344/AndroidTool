package me.lwb.androidtool.common.services.bean

import kotlinx.serialization.Serializable

/**
 * Created by ve3344 .
 */
@Serializable
data class AppPackageDetail(
    var packageName: String,
    var label: String,
    var versionName: String,
    var versionCode: Long,
    var firstInstallTime: Long,
    var minSdk: Int,
    var targetSdk: Int,
    var compileSdk: Int,
    var signMd5: String,
    var signSha1: String,
    var signSha256: String,
    var apkFile: String,
    var apkSize: String,
)
