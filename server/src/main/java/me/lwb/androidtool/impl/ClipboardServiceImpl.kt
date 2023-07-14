package me.lwb.androidtool.impl

import android.content.ClipData
import com.google.auto.service.AutoService
import me.lwb.jsonrpc.RpcMethod
import me.lwb.jsonrpc.RpcService
import me.lwb.androidtool.android.AppManagers
import me.lwb.androidtool.common.services.ClipboardService

/**
 * Created by ve3344 .
 */
@AutoService(RpcService::class)
open class ClipboardServiceImpl : ClipboardService {
    private val clipboardManager get() = AppManagers.clipboardManager

    @RpcMethod
    override fun getClipboard(): String {
        val primaryClip = clipboardManager.getPrimaryClip() ?: return ""
        return primaryClip.getItemAt(0)?.text?.toString() ?: ""
    }

    @RpcMethod
    override fun setClipboard(text: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text))

    }
}