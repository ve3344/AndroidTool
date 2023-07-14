package me.lwb.androidtool.common.utils
import java.security.MessageDigest

/**
 * 摘要算法
 * Created by ve3344 .
 */

/**
 * 字节数组转md5字符串
 * @return 结果字符串，当算法转换失败时为 ""
 */
fun ByteArray.md5(): String = digest("MD5")

/**
 * 字节数组转sha1字符串
 * @return 结果字符串，当算法转换失败时为 ""
 */
fun ByteArray.sha1(): String = digest("SHA-1")

/**
 * 字节数组转sha256字符串
 * @return 结果字符串，当算法转换失败时为 ""
 */
fun ByteArray.sha256(): String = digest("SHA-256")

/**
 * 字节数组使用摘要算法转换
 * @return 结果字符串，当算法转换失败时为 ""
 */
fun ByteArray.digest(algorithm: String): String {
    return kotlin.runCatching { MessageDigest.getInstance(algorithm).digest(this)?.hex() }
        .getOrNull() ?: ""
}

/**
 * 字节数组转hex字符串
 * @return hex字符串，当算法转换失败时为 ""
 */
fun ByteArray?.hex(): String {
    this ?: return ""
    return fold(StringBuilder()) { acc, byte ->
        val hexStr = Integer.toHexString(byte.toInt() and (0xFF))
        if (hexStr.length == 1) {
            acc.append('0')
        }
        acc.append(hexStr)
    }.toString()
}