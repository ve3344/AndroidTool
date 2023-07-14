package me.lwb.androidtool.library.platform.swing

import me.lwb.androidtool.data.bean.file.FileItem
import java.awt.datatransfer.*
import java.io.File

/**
 * Created by ve3344 .
 */
open class AdbFileTransferable(private val fileItem: FileItem):Transferable,ClipboardOwner {
    private val remoteFileFlavor =  DataFlavor(File::class.java,"Adb File");

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf(
            DataFlavor.stringFlavor,
            remoteFileFlavor,
            DataFlavor.javaFileListFlavor,
        )
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        return flavor in transferDataFlavors
    }

    override fun getTransferData(flavor: DataFlavor): Any {
        if (flavor==DataFlavor.stringFlavor){
            return fileItem.path
        }
        if (flavor==DataFlavor.javaFileListFlavor){
            return listOf(File(fileItem.path))
        }
        if (flavor==remoteFileFlavor){
            return File("D:\\Projects_Lwb_Test\\AndroidTool\\config\\control.json")
        }
        throw UnsupportedFlavorException(flavor)
    }

    override fun lostOwnership(clipboard: Clipboard?, contents: Transferable?) {

    }
}