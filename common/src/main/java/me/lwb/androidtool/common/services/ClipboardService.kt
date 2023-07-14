package me.lwb.androidtool.common.services

import me.lwb.jsonrpc.RpcService

interface ClipboardService : RpcService {
    override val serviceName: String get() = ClipboardService::class.java.simpleName
    fun getClipboard(): String
    fun setClipboard(text: String)
}