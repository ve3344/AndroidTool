package me.lwb.androidtool.library.rpc

import me.lwb.androidtool.common.rpc.ConsoleRpcClient
import me.lwb.androidtool.common.rpc.proxy
import me.lwb.androidtool.data.bean.DeviceBean
import me.lwb.androidtool.library.adb.IAdb
import me.lwb.androidtool.library.adb.shell
import me.lwb.androidtool.library.android.AndroidJsSession
import me.lwb.jsonrpc.RpcService
import me.lwb.jsonrpc.net.RpcClient
import me.lwb.logger.loggerForClass
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.properties.ReadOnlyProperty

/**
 * Created by ve3344 .
 */
class AdbRpcServiceFactory(val adb: IAdb) : AutoCloseable {
    private val logger = loggerForClass()
    private val deviceManagerMap: MutableMap<DeviceBean, DeviceManager> = ConcurrentHashMap()


    fun <T : RpcService> getService(deviceBean: DeviceBean, clazz: Class<T>): T {
        val manager = deviceManagerMap.getOrPut(deviceBean) {
            DeviceManager(adb, deviceBean)
        }
        return manager.getClient().proxy(clazz)
    }

    inline fun <reified T : RpcService> serviceOf(crossinline deviceBean: () -> DeviceBean) =
        ReadOnlyProperty<Any?, T> { _, _ ->
            getService(deviceBean(), T::class.java)
        }

    override fun close() {
        deviceManagerMap.values.forEach { it.close() }
        deviceManagerMap.clear()
    }

    class DeviceManager(val adb: IAdb, val deviceBean: DeviceBean) : AutoCloseable {
        private val clients: MutableList<CacheRpcClient> = ArrayList()

        val maxClient = 4

        @Synchronized
        fun getClient(): CacheRpcClient {
            val client = clients.minByOrNull { it.activeRequestCount } ?: return createClient()

            if (client.activeRequestCount >= 1 && clients.size < maxClient) {
                return createClient()
            }
            return client
        }

        private fun createClient(): CacheRpcClient {

            return AndroidJsSession(adb, deviceBean).open(PROGRAM).let {
                val cacheRpcClient =
                    CacheRpcClient(ConsoleRpcClient(it.outputStream, it.inputStream))
//                cacheRpcClient.pid = cacheRpcClient.proxy(PingService::class.java).pid()
                if (DEBUG){
                    thread {
                        kotlin.runCatching { it.errorStream.copyTo(System.err) }
                            .onFailure { it.printStackTrace() }
                    }
                }


                cacheRpcClient
            }.also { clients.add(it) }
        }

        override fun close() {
            adb.shell("ps -ef | grep $PROGRAM | awk '{print \$2}' | xargs kill -9", deviceBean)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class CacheRpcClient(private val rpcClient: RpcClient) {
        private val cacheService: MutableMap<Class<*>, RpcService> = ConcurrentHashMap()

        var pid = -1
        val activeRequestCount get() = rpcClient.activeRequestCount
        fun <T : RpcService> proxy(clazz: Class<T>): T =
            cacheService.getOrPut(clazz) { rpcClient.proxy(clazz) } as T
    }

    companion object {
        private val DEBUG: Boolean = true
        const val PROGRAM = "me.lwb.androidtool.RpcServer"
    }
}