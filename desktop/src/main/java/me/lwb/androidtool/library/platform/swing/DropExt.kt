package me.lwb.androidtool.library.platform.swing

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DropTargetDropEvent
import java.io.File

/**
 * Created by ve3344 .
 */
fun DropTargetDropEvent.files()= transferable
    .transferDataFlavors
    .asSequence()
    .filter { it == DataFlavor.javaFileListFlavor }
    .map { transferable.getTransferData(it) as List<*> }
    .flatten()
    .map { File(it.toString()) }
    .toList()
