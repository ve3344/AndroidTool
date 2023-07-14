package me.lwb.androidtool.data.bean


data class DeviceBean(
    val name: String,
    val status: String,
    val product: String,
    val model: String,
    val device: String,
    val transportId: String,
) {
    companion object {
        //192.168.65.22:5555     device product:PD2055 model:V2055A device:PD2055 transport_id:1
        fun parse(line: String): DeviceBean? {
            return kotlin.runCatching {
                if (line.isBlank()) {
                    return null
                }
                if ("transport_id" !in line){
                    return null
                }
                val propList = line.split("\\s+".toRegex())
                val propMap = propList.fold(mutableMapOf<String, String>()) { acc, item ->
                    if (":" in item) {
                        val (k, v) = item.split(":")
                        acc[k] = v
                    }
                    acc
                }
                DeviceBean(
                    propList[0],
                    propList[1],
                    propMap["product"] ?: "",
                    propMap["model"] ?: "",
                    propMap["device"] ?: "",
                    propMap["transport_id"] ?: "",
                )
            }.onFailure { it.printStackTrace() }
                .getOrNull()
        }
    }
}
