package me.lwb.androidtool.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.local.LocalSetting
import me.lwb.androidtool.library.adb.AdbInstallUtils
import me.lwb.androidtool.library.adb.IAdb
import me.lwb.androidtool.library.adb.LocalAdb
import me.lwb.androidtool.library.adb.ShellResult
import me.lwb.androidtool.library.adb.shell
import me.lwb.androidtool.library.platform.jvm.ShutdownHooks
import me.lwb.androidtool.library.rpc.AdbRpcServiceFactory
import me.lwb.logger.loggerForClass

object AdbManager {

    private val logger = loggerForClass()

    private var localAdb = LocalAdb(LocalSetting.adbPath)

    val adb: IAdb = localAdb

    val adbRpcServiceFactory = AdbRpcServiceFactory(adb)

    private val _devices: MutableStateFlow<List<DeviceBean>> = MutableStateFlow(emptyList())
    private val _currentDevice: MutableStateFlow<DeviceBean?> = MutableStateFlow(null)

    val devices: StateFlow<List<DeviceBean>> = _devices
    val currentDevice: StateFlow<DeviceBean?> = _currentDevice

    fun changeAdbExecutable(adbExecutable: String) {
        checkAdbExecutableValid(adbExecutable)
        localAdb.program = adbExecutable
        adbRpcServiceFactory.close()
    }

    private fun checkAdbExecutableValid(adbExecutable: String) {
        check(AdbInstallUtils.isAdbPathValid(adbExecutable)){"无效的ADB路径"}
    }

    fun setDevices(devices: List<DeviceBean>) {
        _devices.value = devices
        if (currentDevice.value == null) {
            devices.firstOrNull()?.let {
                changeCurrent(it)
            }
        } else if (devices.isEmpty()) {
            _currentDevice.value = null

        }
    }

    fun changeCurrent(deviceBean: DeviceBean) {
        _currentDevice.value = deviceBean
    }


    fun requireDevice(): DeviceBean {
        return (currentDevice.value) ?: throw NoSelectedDeviceException()
    }


    suspend fun loadDevices() = withAdb { it.devices() }
    suspend fun shell(command: String) =
        withAdb { it.shell(command, requireDevice()) }

    @Throws(ExecCommandFailException::class)
    suspend fun awaitShell(command: String) =
        shell(command).let {
            if (it.statusCode != 0) {
                throw ExecCommandFailException(it)
            }
            it.output
        }


    suspend fun pull(remote: String, local: String) =
        withAdb { it.pull(requireDevice(), remote, local) }

    suspend fun push(local: String, remote: String) =
        withAdb { it.push(requireDevice(), local, remote) }

    suspend fun install(file: String): String =
        withAdb { it.install(requireDevice(), file) }

    suspend fun mdnsServices() = withAdb { it.mdnsServices() }
    suspend fun mdnsCheck() = withAdb { it.mdnsCheck() }
    suspend fun pair(deviceAddress: String, password: String) =
        withAdb { it.pair(deviceAddress, password) }

    suspend fun connect(address: String) = withAdb { it.connect(address) }


    init {
        ShutdownHooks.add {
            adbRpcServiceFactory.close()
        }
    }

    class NoSelectedDeviceException() : IllegalStateException("没有链接设备")
    class ExecCommandFailException(val result: ShellResult) : IllegalStateException(result.error)

}