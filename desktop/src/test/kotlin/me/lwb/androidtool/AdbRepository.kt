package me.lwb.androidtool

import kotlinx.coroutines.runBlocking
import me.lwb.androidtool.data.repository.DeviceRepository

/**
 * Created by ve3344 .
 */
fun main() {
    runBlocking {
        val repository = DeviceRepository()
        val message = repository.loadDevices()
        println(message)
        println(repository.loadDeviceProp())
    }
}