package me.lwb.androidtool.common.services.bean

import kotlinx.serialization.Serializable
@Serializable
data class DisplayInfo(
    var displayId: Int,
    var size: Size,
    var rotation: Int,
    var layerStack: Int,
    var flags: Int
) {

    companion object {
        const val FLAG_SUPPORTS_PROTECTED_BUFFERS = 0x00000001
    }
}