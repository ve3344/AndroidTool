package me.lwb.androidtool.common.utils

/**
 * Created by ve3344 .
 */
fun ByteArray.encodeBase64()= Base64.encodeBase64String(this, false)
fun String.decodeBase64()= Base64.decodeBase64(this)