package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.config.Config
import me.lwb.androidtool.data.bean.ExpandStatus
import me.lwb.androidtool.data.bean.FileItemBean
import me.lwb.androidtool.data.bean.file.FileItem
import me.lwb.androidtool.data.repository.FileRepository
import me.lwb.androidtool.library.adb.ShellResult
import me.lwb.androidtool.ui.core.GlobalUi
import java.io.File

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class FileViewModel() : BaseViewModel() {

    val repository = FileRepository()
    val fileList: MutableStateFlow<List<FileItemBean>> = MutableStateFlow(emptyList())
    val fileClipboard: MutableStateFlow<FileItemBean?> = MutableStateFlow(null)

    fun toggle(itemBean: FileItemBean) {
        launch {
            fileList.value = repository.awaitToggle(fileList.value, itemBean)
        }
    }


    init {
        launch {
            currentDevice.filterNotNull().collect {
                loadRootFileList()
            }
        }
    }

    fun loadRootFileList() {
        launch {
            val file = repository.loadFileList(null, Config.DEFAULT_FILE_ROOT).firstOrNull() ?: return@launch
            fileList.value = listOf(FileItemBean(file, null))
        }
    }

    fun syncDir(itemBean: FileItemBean?) {
        itemBean ?: return
        launch {
            var currentFileList = fileList.value

            if (itemBean.expandStatus is ExpandStatus.Expanded) {
                currentFileList = repository.awaitToggle(currentFileList, itemBean)
            }
            fileList.value = repository.awaitToggle(currentFileList, itemBean)
        }
    }

    fun removeFile(itemBean: FileItemBean) {
        launch {
            val output: ShellResult = repository.shell("rm ${itemBean.path}")
            println(output)
            GlobalUi.showToast(if (output.statusCode == 0) output.output else output.error)
            syncDir(itemBean.parent)
        }
    }

    fun renameFile(itemBean: FileItemBean, newName: String) {
        if (newName == itemBean.name) {
            return
        }
        val newPath = (itemBean.parent?.path ?: "") + "/" + newName
        launch {
            val output: ShellResult =
                repository.shell("mv ${itemBean.path} $newPath")
            println(output)
            GlobalUi.showToast(if (output.statusCode == 0) output.output else output.error)
            syncDir(itemBean.parent)
        }
    }

    fun clearClipboard() {
        fileClipboard.value = null
    }

    fun setClipboard(itemBean: FileItemBean) {
        fileClipboard.value = itemBean
    }

    fun push(files: List<File>, dir: FileItemBean?) {
        dir ?: return
        launch {
            for (file in files) {
                repository.push(file.absolutePath, dir.path + "/" + file.name)
            }
            syncDir(dir)
        }
    }

    companion object {
        private suspend fun FileRepository.awaitToggle(
            current: List<FileItemBean>,
            itemBean: FileItemBean,
        ): List<FileItemBean> {
            val index = itemBean.index
            val expandStatus = itemBean.expandStatus
            return when (expandStatus) {
                is ExpandStatus.Normal -> {
                    val list: List<FileItem> =
                        loadFileList(itemBean.file, itemBean.path + "/")
                    val files = list.map { fileItem -> FileItemBean(fileItem, itemBean) }
                    itemBean.expandStatus = ExpandStatus.Expanded(files)

                    ArrayList(current).apply {
                        addAll(index + 1, files)
                    }
                }

                is ExpandStatus.Expanded -> {
                    val totalChildrenCount = expandStatus.totalChildrenCount()
                    val list = current.subList(
                        0,
                        index + 1
                    ) + current.subList(index + 1 + totalChildrenCount, current.size)
                    itemBean.expandStatus = ExpandStatus.Normal
                    list
                }
            }
        }
    }
}