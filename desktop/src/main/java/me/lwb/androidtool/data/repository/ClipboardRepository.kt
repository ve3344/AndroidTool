package me.lwb.androidtool.data.repository

import me.lwb.androidtool.base.withRpc
import me.lwb.androidtool.common.services.ClipboardService

class ClipboardRepository() {
    suspend fun getClipboard() = withRpc<ClipboardService,String> { it.getClipboard() }
    suspend fun setClipboard(text:String) = withRpc<ClipboardService,Unit> { it.setClipboard(text) }

}


