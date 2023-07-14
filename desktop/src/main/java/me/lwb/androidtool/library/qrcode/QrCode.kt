package me.lwb.androidtool.library.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.IOException
import java.util.*

object QrCode{
    @Throws(WriterException::class, IOException::class)
    fun encode(content: String, size: Int = 0): BitMatrix {
        val encodeHints: MutableMap<EncodeHintType, Any> =
            EnumMap(EncodeHintType::class.java)
        encodeHints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        encodeHints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        return MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            encodeHints
        )


    }
}
