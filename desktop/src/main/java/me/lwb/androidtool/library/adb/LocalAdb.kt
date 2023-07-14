package me.lwb.androidtool.library.adb

import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.bean.MdnsService
import me.lwb.androidtool.data.bean.PairResult
import me.lwb.androidtool.library.shell.Shell
import me.lwb.androidtool.library.shell.close
import me.lwb.logger.loggerForClass
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

/**
 * Created by ve3344 .
 */
open class LocalAdb(var program: String = "adb.exe") : IAdb {
    val logger = loggerForClass()
    override fun devices(): List<DeviceBean> {
        return exec("devices -l").getLines { DeviceBean.parse(it) }
    }

    override fun pull(deviceBean: DeviceBean?, remote: String, local: String) {
        exec("pull $remote $local", deviceBean).waitResult()
    }

    override fun push(deviceBean: DeviceBean?, local: String, remote: String) {
        exec("push $local $remote", deviceBean).waitResult()
    }

    override fun forward(deviceBean: DeviceBean?, local: String, remote: String) {
        exec("forward $local $remote", deviceBean).waitResult()

    }

    override fun openShell(deviceBean: DeviceBean?): IAdbStream {
        return exec("shell", deviceBean)
    }

    override fun install(deviceBean: DeviceBean?, file: String): String {
        val exec = exec("install \"$file\"", deviceBean)
        val result = exec.waitResult()
        if (result == 0) {
            return ""
        }
        return exec.getError()
    }

    private fun exec(
        command: String,
        deviceBean: DeviceBean? = null,
    ): IAdbStream {
        val deviceOption = deviceBean?.let { " -t ${it.transportId}" } ?: ""
        val exec = "$program$deviceOption $command"
        logger.d("exec [$exec]")
        val process = Shell.exec(exec)
        return LocalStreamWrapper(process)
    }

    private class LocalStreamWrapper(val process: Process) : IAdbStream {
        override val inputStream: InputStream
            get() = process.inputStream
        override val outputStream: OutputStream
            get() = process.outputStream
        override val errorStream: InputStream
            get() = process.errorStream

        override fun close() {
            process.close()
        }

        override fun waitResult(timeout: Long): Int {
            return if (timeout <= 0) {
                process.waitFor()
            } else {
                if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
                    return IAdbStream.EXEC_TIMEOUT
                }
                process.exitValue()
            }
        }

    }

    override fun mdnsCheck(): Boolean {
        val stream = exec("mdns check")
        val ret = stream.waitResult()
        logger.d("mdnsCheck$ret ")

        return stream.getOutput().contains("version")
    }


    override fun mdnsServices(): List<MdnsService> {
        val stream = exec("mdns services")
//        val lineRegex = Regex("([^\\t]+)\\t*_adb-tls-pairing._tcp.\\t*([^:]+):([0-9]+)")

        return stream.getLines { line ->
            try {
                val split = line.split("\\s+".toRegex())
                MdnsService(split[0].trim(), split[1].trim(), split[2].trim())
            } catch (ignored: Exception) {
                ignored.printStackTrace()
                null
            }
        }
    }

    override fun pair(deviceAddress: String, code: String): PairResult? {
        val exec = exec("pair $deviceAddress $code")
        val result = exec.waitResult()
        if (result != 0) {
            return null
        }

        val output = exec.getOutput()
        if (output.isEmpty()) {
            return null
        }
        val lineRegex = Regex("Successfully paired to ([^:]*):([0-9]*) \\[guid=([^\\]]*)\\]")
        return lineRegex.find(output)?.let {
            kotlin.runCatching {
                val ipAddress = it.groupValues[1]
                val port = it.groupValues[2].toInt()
                val serviceGuid = it.groupValues[3]
                PairResult("$ipAddress:$port", serviceGuid)
            }.getOrNull()
        }
    }

    override fun connect(address: String): PairResult? {
        val exec = exec("connect $address")
        val result = exec.waitResult()
        if (result != 0) {
            return null
        }
        val output = exec.getOutput()
        if (output.isEmpty()) {
            return null
        }
        if (output.startsWith("connected to ")) {
            return PairResult(output.substringAfter("connected to ").trim(), "")
        }
        return null

        //connected to 192.168.0.5:42283

    }
}

