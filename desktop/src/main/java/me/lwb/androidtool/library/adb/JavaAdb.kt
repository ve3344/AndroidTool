package me.lwb.androidtool.library.adb

import dadb.AdbShellPacket
import dadb.AdbShellStream
import dadb.Dadb
import dadb.ID_STDIN
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.data.bean.MdnsService
import me.lwb.androidtool.data.bean.PairResult
import java.io.*
import kotlin.concurrent.thread

/**
 * Created by ve3344 .
 */
open class JavaAdb : IAdb {

    private val map= hashMapOf<String,Dadb>()
    private fun getDadb(deviceBean: DeviceBean?): Dadb {
        deviceBean?: return Dadb.list().first()
        return map[deviceBean.transportId]?:Dadb.list().first { it.hashCode().toString()==deviceBean.transportId }
    }

    override fun mdnsCheck(): Boolean {
        TODO("Not yet implemented")
    }

    override fun mdnsServices(): List<MdnsService> {
        TODO("Not yet implemented")
    }

    override fun pair(deviceAddress: String, code: String): PairResult? {
        TODO("Not yet implemented")
    }

    override fun connect(address: String): PairResult? {
        val (ip,port)= address.split(":")
        Dadb.create(ip, port.toIntOrNull() ?: 5555)

        return PairResult(address,"UNKNOWN")
    }

    override fun forward(deviceBean: DeviceBean?, local: String, remote: String) {
        getDadb(deviceBean).tcpForward(local.toInt(),remote.toInt())
    }

    override fun openShell(deviceBean: DeviceBean?): IAdbStream {
        val shellStream: AdbShellStream = getDadb(deviceBean).openShell()
        return ShellStreamWrapper(shellStream)
    }

    override fun devices(): List<DeviceBean> {
        return Dadb.list().map {
            val out = it.shell("getprop ro.product.model").output

            DeviceBean(out, out, out, out, out, it.hashCode().toString())
        }
    }

    override fun install(deviceBean: DeviceBean?, file: String): String {
        return try {
            getDadb(deviceBean).install(File(file))
            ""
        } catch (e: Exception) {
            e.message?:e.toString()
        }
    }

    override fun pull(deviceBean: DeviceBean?, remote: String, local: String) {
        getDadb(deviceBean).pull(File(local),remote)
    }

    override fun push(deviceBean: DeviceBean?, local: String, remote: String) {
        getDadb(deviceBean).pull(File(local),remote)

    }

    class ShellStreamWrapper(val s: AdbShellStream) : IAdbStream {
        private val inputStreamPipe = PipedOutputStream()
        private val errorStreamPipe = PipedOutputStream()
        private var closed = false

        override val inputStream: InputStream = PipedInputStream().apply {
            connect(inputStreamPipe)
        }
        override val errorStream: InputStream = PipedInputStream().apply {
            connect(errorStreamPipe)
        }
        override val outputStream: OutputStream = object : OutputStream() {
            override fun write(b: Int) {
                write(byteArrayOf(b.toByte()))
            }

            override fun write(b: ByteArray) {
                s.write(ID_STDIN, b)
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                write(b.copyOfRange(off, off + len))
            }
        }

        private var exitCode: Int? = null
        private val lock: Object = Object()

        @Throws(InterruptedException::class)

        override fun waitResult(timeout: Long): Int {
            exitCode?.let {
                return it
            }
            if (timeout <= 0) {
                synchronized(lock) {
                    lock.wait()
                }
            } else {
                synchronized(lock) {
                    lock.wait(timeout)
                }
            }
            return exitCode ?: IAdbStream.EXEC_TIMEOUT
        }

        init {
            thread {
                while (!closed) {
                    when (val packet = s.read()) {
                        is AdbShellPacket.Exit -> {
                            exitCode = packet.payload[0].toInt()
                            synchronized(lock) {
                                lock.notifyAll()
                            }
                            break
                        }

                        is AdbShellPacket.StdOut -> {
                            inputStreamPipe.write(packet.payload)
                        }

                        is AdbShellPacket.StdError -> {
                            errorStreamPipe.write(packet.payload)
                        }
                    }
                }
            }
        }

        override fun close() {
            closed = true
            s.close()
        }
    }
}