package me.lwb.androidtool.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.withAdb
import me.lwb.androidtool.data.bean.InfoConfigBean

class PairRepository() {
    suspend fun pair(deviceAddress: String, password: String) =
        AdbManager.pair(deviceAddress, password)

    suspend fun mdnsServices() = AdbManager.mdnsServices()
    suspend fun mdnsCheck() = AdbManager.mdnsCheck()
    suspend fun devices() = AdbManager.loadDevices()
    suspend  fun connect(address: String)  =
        AdbManager.connect(address)

}


