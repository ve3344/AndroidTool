package me.lwb.androidtool.library.qrcode

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.google.zxing.common.BitMatrix

class QrCodePainter(
    private val matrix: BitMatrix,
    val bitColor: Color=Color.Black,
    size: Size = Size.Unspecified,
    val cornerRadius: CornerRadius= CornerRadius.Zero,
) : Painter() {
    override val intrinsicSize: Size = size

    override fun DrawScope.onDraw() {
        if (matrix.width == 0) {
            return
        }
        val itemWidth: Float = size.width / matrix.width.toFloat()
        val itemSize = Size(itemWidth, itemWidth)

        repeat(matrix.height) { y ->
            val top = y.toFloat() * itemWidth

            repeat(matrix.width) { x ->
                val left: Float = x.toFloat() * itemWidth

                val bit = matrix.get(x, y)

                if (bit) {
                    drawRoundRect(bitColor, Offset(top, left), itemSize, cornerRadius)
                }

            }
        }
    }
}
