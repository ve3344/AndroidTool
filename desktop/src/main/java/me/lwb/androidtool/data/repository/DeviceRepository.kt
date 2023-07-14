package me.lwb.androidtool.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.data.bean.InfoConfigBean

class DeviceRepository() {

    suspend fun loadDevices() = AdbManager.loadDevices()
    suspend fun loadDeviceProp(): Map<String, String> =
        withContext(Dispatchers.IO){
            AdbManager.shell("getprop")
                .output
                .lineSequence()
                .mapNotNull { parseProp(it) }
                .toMap()
        }

    private fun parseProp(line: String): Pair<String, String>? {
        return kotlin.runCatching {
            val sp = line.split(": ")
            sp[0].unwrapProp() to sp[1].unwrapProp()
        }.getOrNull()
    }

    private fun String.unwrapProp(): String {
        return trim().substring(1 until (length - 1))
    }


    suspend fun loadInfoConfig(path: String): List<InfoConfigBean> {
        return withContext(Dispatchers.IO) {
            InfoConfigBean.loadInfoConfig(path).filterNot { it.key.isNullOrBlank() }
        }
    }


    suspend fun install(path: String): String = AdbManager.install(path)


}


