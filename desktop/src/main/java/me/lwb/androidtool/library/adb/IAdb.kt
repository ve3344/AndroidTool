package me.lwb.androidtool.library.adb

import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.bean.MdnsService
import me.lwb.androidtool.data.bean.PairResult

/**
 * Created by ve3344 .
 */
interface IAdb {
    fun devices(): List<DeviceBean>
    fun openShell(deviceBean: DeviceBean?): IAdbStream
    fun install(deviceBean: DeviceBean?, file: String): String
    fun pull(deviceBean: DeviceBean?, remote: String, local: String)
    fun push(deviceBean: DeviceBean?, local: String, remote: String)

    //forward [--no-rebind] LOCAL REMOTE
    fun forward(deviceBean: DeviceBean?,local: String, remote: String)
    fun mdnsCheck():Boolean
    fun mdnsServices(): List<MdnsService>
    fun pair(deviceAddress: String, code: String): PairResult?
    fun connect(address: String):PairResult?
    //mdns services
}