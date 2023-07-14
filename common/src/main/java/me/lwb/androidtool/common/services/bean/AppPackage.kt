package me.lwb.androidtool.common.services.bean

import kotlinx.serialization.Serializable

/**
 * Created by ve3344 .
 */

@Serializable
data class AppPackage(
    var packageName:String,
    var label:String,
    var versionName:String,
)
