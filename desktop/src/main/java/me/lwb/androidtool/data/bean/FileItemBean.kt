package me.lwb.androidtool.data.bean

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.lwb.androidtool.data.bean.file.FileItem

/**
 * Created by ve3344 .
 */

sealed class ExpandStatus {
    class Expanded(val children: List<FileItemBean>) : ExpandStatus(){
        fun totalChildrenCount():Int{
            var total=0
            for (child in children) {
                val expandStatus = child.expandStatus
                total+=when (expandStatus){
                    is Expanded -> 1 + expandStatus.totalChildrenCount()
                    Normal -> 1
                }
            }
            return total
        }
    }
    object Normal : ExpandStatus()
}

data class FileItemBean(val file: FileItem, val parent: FileItemBean? = null) {
    var index: Int = 0
    val level: Int = if (parent == null) 0 else parent.level + 1

    val name: String get() = file.name

    val path: String = file.path

    var expandStatus: ExpandStatus by mutableStateOf(ExpandStatus.Normal)

    val ext: String get() = file.name.substringAfterLast(".").lowercase()

    val isDir get() = file.isDirectory

    val canExpand get() = file.hasChildren

    val allParent
        get() = sequence<FileItemBean> {
            var p=parent
            while (p!=null){
                yield(p)
                p=p.parent
            }
        }
}