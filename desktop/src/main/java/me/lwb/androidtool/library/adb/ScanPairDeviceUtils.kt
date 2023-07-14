package me.lwb.androidtool.library.adb

import java.util.*
import kotlin.random.asKotlinRandom

object ScanPairDeviceUtils {
    private const val studioServiceNamePrefix = "studio-"
    fun generatePassword() = createRandomString(12)

    fun generateQrCode(password: String): String {
        val serviceName = studioServiceNamePrefix + createRandomString(10)
        return createPairingString(serviceName, password)
    }

    private fun createPairingString(service: String, password: String): String {
        return "WIFI:T:ADB;S:${service};P:${password};;"
    }

    @Suppress("SpellCheckingInspection")
    private fun createRandomString(charCount: Int): String {
        val charSet =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-+*/<>{}"
        val random = Random().asKotlinRandom()
        return buildString {
            repeat(charCount) {
                append(charSet.random(random))
            }

        }
    }




}