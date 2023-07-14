package me.lwb.androidtool.data.bean

/**
 * Created by ve3344 .
 */
//serviceName, serviceType, ipAddress, port
data class MdnsService(
    val serviceName:String,
    val serviceType:String,
    val address:String,
    ) {

    val pairServiceType :PairServiceType?
        get(){
            if (serviceType== ADB_PAIR){
               return if (serviceName.startsWith(studioServiceNamePrefix)) PairServiceType.QrCode else PairServiceType.PairingCode
            }else{
                return null
            }
        }

    companion object{
        const val ADB_PAIR="_adb-tls-pairing._tcp"
        const val ADB_CONNECT="_adb-tls-connect._tcp"
        private val studioServiceNamePrefix = "studio-"

    }
    enum class PairServiceType{
        QrCode,
        PairingCode,
    }
}