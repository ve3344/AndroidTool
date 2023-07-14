package me.lwb.androidtool.library.apksign

import me.lwb.logger.loggerForClass
import me.lwb.androidtool.library.shell.awaitShell
import java.io.File

/**
 * Created by ve3344 .
 */
open class ApkSigner {
    val logger = loggerForClass()
    val zipalign = "D:\\AndroidSdk\\build-tools\\28.0.3\\zipalign.exe"
    val jarsigner = "jarsigner"
    val apksigner = "java -jar D:\\AndroidSDK\\build-tools\\28.0.3\\lib\\apksigner.jar"
    val keytool = "keytool"

    suspend fun zipalign(src: String, dst: String) = awaitShell {
        """
    $zipalign -f -v 4 $src $dst
""".trimIndent()
    }

    suspend fun signV1(keystore: Keystore, src: String, dst: String) = awaitShell {
        """
    $jarsigner -verbose 
    -keystore ${keystore.filePath}
    -signedjar             
    $dst                         
    $src
    """
    }

   private suspend fun signV2(keystore: Keystore, src: String, dst: String) = awaitShell {
        """
    $apksigner sign 
    -verbose 
    --ks "${keystore.filePath}"
    --ks-pass pass:${keystore.storePass}
    --ks-key-alias "${keystore.alias}"
    --key-pass pass:${keystore.aliasPass}
    --out "$dst" 
    "$src"
    """
    }


    suspend fun list(keystore: Keystore) = awaitShell {
        "$keytool -v -list -keystore ${keystore.filePath}"
    }

    suspend fun verify(src: String) = awaitShell {
        "$apksigner verify --verbose $src"
    }


    suspend fun signV2Normal(keystore: Keystore, src: String, dstDir: String) {
        val tmp = src.replace(".apk", "_align.apk")
        val dst = File(dstDir, File(tmp).name.replace(".apk", "_signed.apk")).absolutePath
        File(tmp).deleteOnExit()
        zipalign(src, tmp)
        signV2(keystore, tmp, dst)
        File(tmp).delete()
    }


}

