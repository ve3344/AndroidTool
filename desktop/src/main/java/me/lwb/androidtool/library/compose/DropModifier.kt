package me.lwb.androidtool.library.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.*
import javax.swing.JPanel
import kotlin.math.roundToInt


class SimpleDropTarget(
    private val onDragEnter: ((DropTargetDragEvent) -> Unit)? = null,
    private val onDragExit: ((DropTargetEvent) -> Unit)? = null,
    private val onDragOver: ((DropTargetDragEvent) -> Unit)? = null,
    private val onDrop: ((DropTargetDropEvent) -> Unit)? = null,
) :
    DropTarget() {
    override fun dragEnter(dtde: DropTargetDragEvent) {
        onDragEnter?.invoke(dtde)
    }

    override fun dragOver(dtde: DropTargetDragEvent) {
        onDragOver?.invoke(dtde)
    }

    override fun dragExit(dte: DropTargetEvent) {
        onDragExit?.invoke(dte)
    }

    override fun drop(dtde: DropTargetDropEvent) {
        onDrop?.invoke(dtde)
    }
}

/**
 *
 */
fun Modifier.onDrop(
    window: ComposeWindow,
    enabled: Boolean,
    onDrop: (DropTargetDropEvent) -> Unit,
): Modifier = composed {
    Modifier.onDrop(
        window = window,
        dropTarget = SimpleDropTarget(onDrop = onDrop),
        enabled = enabled,
        indication = LocalIndication.current,
        mutableInteractionSource = remember { MutableInteractionSource() }
    )
}
fun Modifier.onDrop(
    window: ComposeWindow,
    enabled: Boolean,
    dropTarget: DropTarget,
): Modifier = composed {
    Modifier.onDrop(
        window = window,
        dropTarget = dropTarget,
        enabled = enabled,
        indication = LocalIndication.current,
        mutableInteractionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.onDrop(
    window: ComposeWindow,
    enabled: Boolean,
    dropTarget: DropTarget,
    indication: Indication,
    mutableInteractionSource: MutableInteractionSource,
): Modifier = composed(
    factory = {

        val component = remember(dropTarget) {
            JPanel().apply {
                this.dropTarget = dropTarget
                isOpaque = true
            }
        }
        DisposableEffect(window.rootPane, component) {
            window.rootPane.add(component)
            onDispose {
                runCatching {
                    window.rootPane.remove(component)
                }
            }
        }
        val density = LocalDensity.current.density
        Modifier
            .indication(mutableInteractionSource, indication)
            .hoverable(enabled = enabled, interactionSource = mutableInteractionSource)
            .onGloballyPositioned {
                val x = it.positionInWindow().x / density
                val y = it.positionInWindow().y / density
                val width = it.size.width / density
                val height = it.size.height / density
                component.setBounds(x.roundToInt(),
                    y.roundToInt(),
                    width.roundToInt(),
                    height.roundToInt())
            }


    }
)

class FileTransferable() :
    Transferable {

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf(DataFlavor.javaFileListFlavor)
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        return true
    }

    override fun getTransferData(flavor: DataFlavor): Any {
        return "ok"
    }


}


fun Modifier.onDrag(
    window: ComposeWindow,
    enabled: Boolean,
    onDrag: (DragGestureEvent) -> Boolean,
): Modifier = composed {
    Modifier.onDrag(
        window = window,
        onDrag = onDrag,
        enabled = enabled,
        indication = LocalIndication.current,
        mutableInteractionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.onDrag(
    window: ComposeWindow,
    enabled: Boolean,
    onDrag: (DragGestureEvent) -> Boolean,
    indication: Indication,
    mutableInteractionSource: MutableInteractionSource,
): Modifier = composed(
    factory = {

        val component = remember {
            JPanel().apply {
                val dragSource = DragSource()
                dragSource.createDefaultDragGestureRecognizer(this,
                    DnDConstants.ACTION_COPY_OR_MOVE) {
                    onDrag(it)
                }
                isOpaque = true
            }
        }
        val pane = remember {
            window.rootPane
        }
        DisposableEffect(true) {
            pane.add(component)
            onDispose {
                runCatching {
                    pane.remove(component)
                }
            }
        }
        val density = LocalDensity.current.density
        Modifier
            .indication(mutableInteractionSource, indication)
            .hoverable(enabled = enabled, interactionSource = mutableInteractionSource)
            .onGloballyPositioned {
                val x = it.positionInWindow().x / density
                val y = it.positionInWindow().y / density
                val width = it.size.width / density
                val height = it.size.height / density
                component.setBounds(x.roundToInt(),
                    y.roundToInt(),
                    width.roundToInt(),
                    height.roundToInt())
            }


    }
)