package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.lwb.androidtool.base.AdbManager
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.bean.MdnsService
import me.lwb.androidtool.data.bean.PairResult
import me.lwb.androidtool.data.repository.PairRepository
import me.lwb.androidtool.library.adb.ScanPairDeviceUtils

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class PairViewModel : BaseViewModel() {
    private val repository = PairRepository()

    val pairPageVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val scanPairStatus: MutableStateFlow<ScanPairStatus?> = MutableStateFlow(null)

    val codePairingServices: MutableStateFlow<List<MdnsService>> = MutableStateFlow(emptyList())

    private var scanPairJob:Job?=null
    private var discoveryCodePairingServicesJob:Job?=null
    fun discoveryCodePairingServices() {
        discoveryCodePairingServicesJob?.cancel()
        discoveryCodePairingServicesJob=launch {
            while (true) {
                codePairingServices.value = repository.mdnsServices()
                delay(2000)
            }
        }

    }

    fun hidePairPage() {
        if (!pairPageVisible.value){
            return
        }
        pairPageVisible.value = false
        discoveryCodePairingServicesJob?.cancel()

        cancelScanPair()
    }
    fun showPairPage() {
        if (pairPageVisible.value){
            return
        }
        pairPageVisible.value = true

        startScanPair()
        discoveryCodePairingServices()

    }


    fun startScanPair() {
        cancelScanPair()
        scanPairJob=launch {
            try {
                check(repository.mdnsCheck()) { "mdnsCheck" }
                val password = ScanPairDeviceUtils.generatePassword()
                val qrCode = ScanPairDeviceUtils.generateQrCode(password)

                scanPairStatus.value =
                    ScanPairStatus.WaitScan(qrCode)
                val pairResult = try {
                    awaitScan(password)
                }catch (e:CancellationException){
                    throw e
                } catch (e: IllegalStateException) {
                    throw IllegalStateException("配对失败",e)
                }
                scanPairStatus.value = ScanPairStatus.WaitDevice()

                val devices = awaitDevice(pairResult)

                AdbManager.setDevices(devices)
            } finally {
                scanPairStatus.value = null
            }
        }
    }

    private suspend fun awaitDevice(pairResult: PairResult): List<DeviceBean> {

        var devices = repository.devices()
        repeat(5) {
            if (devices.any { it.name.startsWith(pairResult.guid) }) {
                return devices
            }
            delay(500)
            devices = repository.devices()
        }

        return devices

    }


    private suspend fun awaitScan(password: String): PairResult {
        while (true) {
            repository.mdnsServices()
                .asFlow()
                .filter { it.pairServiceType == MdnsService.PairServiceType.QrCode }
                .map { repository.pair(it.address, password) }
                .firstOrNull()
                ?.let { return it }

            delay(1000)
        }
    }

    fun cancelScanPair() {
        scanPairJob?.cancel()
        scanPairJob = null
        scanPairStatus.value=null
    }

    fun pair(address: String, password: String) {
        launch {
            check( repository.pair(address, password)!=null){"配对失败"}
            AdbManager.setDevices(repository.devices())
        }
    }

    fun connect(address: String) {
        launch {
            check(repository.connect(address)!=null){"链接失败"}
            AdbManager.setDevices(repository.devices())
        }

    }

    sealed class ScanPairStatus() {
        class WaitScan(val qrCode: String) : ScanPairStatus()
        class WaitDevice() : ScanPairStatus()
    }
}