package me.lwb.androidtool.data.bean

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Created by ve3344 .
 */
//{
//    "title": "Input",
//    "key": "input"
//  }

@Serializable
data class InfoConfigBean @JvmOverloads constructor(
    var title: String? = null,
    var key: String? = null,
) {
    companion object {
        fun loadInfoConfig(path: String): List<InfoConfigBean> = Json.decodeFromString(File(path).readText())
    }
}



