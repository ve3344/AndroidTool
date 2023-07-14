package me.lwb.androidtool.ui.page

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Folder
import me.lwb.androidtool.LocalWindow
import me.lwb.androidtool.Painters
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.data.bean.ExpandStatus
import me.lwb.androidtool.data.bean.FileItemBean
import me.lwb.androidtool.library.compose.FileTransferable
import me.lwb.androidtool.library.compose.SimpleDropTarget
import me.lwb.androidtool.library.compose.onDrag
import me.lwb.androidtool.library.compose.onDrop
import me.lwb.androidtool.library.compose.onMouseLeftClick
import me.lwb.androidtool.library.compose.onMouseRightClick
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.CommonTextField
import me.lwb.androidtool.ui.widget.SimpleDropdownMenuItem
import me.lwb.androidtool.vm.FileViewModel
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.FlavorListener
import java.awt.dnd.DnDConstants
import java.awt.dnd.DragSource
import java.io.File

/**
 * Created by ve3344 .
 */
@AutoService(TabPageHolder::class)
class FilePageTabHolder : TabPageHolder {
    override val tabItem = TabItem("文件", FeatherIcons.Folder, 1) { FilePage() }
}

@Composable
fun FilePage(
    viewModel: FileViewModel = rememberViewModel(),
) {
    val fileClipboard by viewModel.fileClipboard.collectAsState()

    Column {
        val files: List<FileItemBean> by viewModel.fileList.collectAsState()
        Box(Modifier.fillMaxWidth().weight(1f)) {
            val scrollState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState
            ) {
                itemsIndexed(files) { index, it ->
                    FileTreeItemView(index, it, viewModel)
                }
            }


            VerticalScrollbar(
                ScrollbarAdapter(scrollState),
                Modifier.align(Alignment.CenterEnd),
            )
        }

        DisposableEffect(Unit) {
            val systemClipboard = Toolkit.getDefaultToolkit().systemClipboard
            val flavorListener = FlavorListener {
                systemClipboard.availableDataFlavors.forEach {
                    val res = kotlin.runCatching { systemClipboard.getContents(null) }.getOrNull()
                }
            }
            systemClipboard.addFlavorListener(flavorListener)
            onDispose {
                systemClipboard.removeFlavorListener(flavorListener)
            }
        }
        fileClipboard?.let {
            FileClipboard(it) { viewModel.clearClipboard() }
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileClipboard(fileClipboard: FileItemBean, onClose: () -> Unit) {
    Column(Modifier.background(Color.Gray).fillMaxWidth().onDrag {
        println(it)
    }) {
        var removeOrigin by remember { mutableStateOf(false) }
        Text("File:${fileClipboard.path} ")
//        Checkbox(removeOrigin, onCheckedChange = {removeOrigin=!removeOrigin})
        Button({ onClose() }) {
            Text("Close")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun FileTreeItemView(
    index: Int,
    itemBean: FileItemBean,
    viewModel: FileViewModel,
) {
    itemBean.index = index

    var renameDialog by remember { mutableStateOf(false) }
    if (renameDialog) {
        RenameDialog({ renameDialog = false }, itemBean, viewModel)
    }
    var dropFiles: List<File> by remember { mutableStateOf(emptyList()) }
    PushFileConfirmDialog({ dropFiles = emptyList() }, itemBean, viewModel, dropFiles)

    Column {
        var menuOpen by remember { mutableStateOf(false) }
        FileMenu(menuOpen, { menuOpen = false }, itemBean, viewModel, {
            renameDialog = true
        })

        var dropStatus by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .clickable { viewModel.toggle(itemBean) }
                .background(if (dropStatus) ThemeColors.Active else Color.Unspecified)
                .onMouseLeftClick { viewModel.toggle(itemBean) }
                .onMouseRightClick { menuOpen = true }
                .onDrop(LocalWindow.current, true, SimpleDropTarget(
                    onDragEnter = {
                        dropStatus = true
                    },
                    onDragExit = {
                        dropStatus = false
                    },
                    onDrop = { dropEvent ->
                        dropStatus = false
                        dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE)
                        dropFiles = dropEvent.transferable
                            .transferDataFlavors
                            .asSequence()
                            .filter { it == DataFlavor.javaFileListFlavor }
                            .map { dropEvent.transferable.getTransferData(it) as List<*> }
                            .flatten()
                            .map { File(it.toString()) }
                            .toList()
                        dropEvent.dropComplete(true)
                    }
                ))
                .onDrag(LocalWindow.current, true) {
                    it.startDrag(DragSource.DefaultMoveNoDrop, FileTransferable())

                    true
                }
                .padding(start = 24.dp * itemBean.level)
                .fillMaxWidth()
        ) {
            FileItemIcon(Modifier.align(Alignment.CenterVertically), itemBean)
            Text(
                text = itemBean.name,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .clipToBounds(),
                softWrap = true,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            if (itemBean.isDir) {
                Box(Modifier.size(24.dp).padding(4.dp)) {
                    if (itemBean.expandStatus is ExpandStatus.Expanded) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    } else {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PushFileConfirmDialog(
    onDismissRequest: () -> Unit,
    itemBean: FileItemBean,
    viewModel: FileViewModel,
    dropFiles: List<File>
) {
    if (dropFiles.isNotEmpty()) {
        AlertDialog(onDismissRequest, title = {
            Text("确认传输 $dropFiles 到 ${itemBean.path}")
        }, confirmButton = {
            Text("确定", Modifier.clickable {
                if (itemBean.isDir) {
                    viewModel.push(dropFiles, itemBean)
                } else {
                    viewModel.push(dropFiles, itemBean.parent)
                }
                onDismissRequest()
            })
        }, dismissButton = {
            Text("取消", Modifier.clickable {
                onDismissRequest()
            })
        })
    }
}

@Composable
private fun RenameDialog(
    onDismissRequest: () -> Unit,
    itemBean: FileItemBean,
    viewModel: FileViewModel
) {
    Dialog(onDismissRequest) {
        Column {
            Spacer(Modifier.height(10.dp))
            var newName by remember(itemBean.name) { mutableStateOf(itemBean.name) }
            CommonTextField(
                newName, { newName = it },
            )
            Row {
                Button(onDismissRequest) {
                    Text("取消")
                }
                Spacer(Modifier.width(10.dp))
                Button({
                    onDismissRequest()
                    viewModel.renameFile(itemBean, newName)
                }) {
                    Text("确定")
                }
            }

        }

    }

}

@Composable
private fun FileMenu(
    menuVisible: Boolean,
    onDismissMenu: () -> Unit,
    itemBean: FileItemBean,
    viewModel: FileViewModel,
    onRename: () -> Unit
) {
    DropdownMenu(
        expanded = menuVisible,
        onDismissRequest = onDismissMenu,
        offset = DpOffset(10.dp, 10.dp),
    ) {
        val clipboardManager = LocalClipboardManager.current
        SimpleDropdownMenuItem("复制路径", {
            clipboardManager.setText(AnnotatedString(itemBean.path))
            GlobalUi.showToast("复制成功")
            onDismissMenu()
        })
        SimpleDropdownMenuItem("刷新", {
            viewModel.syncDir(itemBean)
            onDismissMenu()
        })
        SimpleDropdownMenuItem("删除", {
            viewModel.removeFile(itemBean)
            onDismissMenu()
        })
        SimpleDropdownMenuItem("重命名", {
            onRename()
            onDismissMenu()
        })
//        SimpleDropdownMenuItem("复制到剪贴板", {
//            onDismissMenu()
//            val systemClipboard = Toolkit.getDefaultToolkit().systemClipboard
//            systemClipboard.setContents(AdbFileTransferable(itemBean.file), null)
//
////                viewModel.setClipboard(itemBean)
//        })
    }
}


@Composable
private fun FileItemIcon(modifier: Modifier, itemBean: FileItemBean) =
    Box(modifier.size(24.dp).padding(4.dp)) {

        if (itemBean.isDir) {
            Image(
                if (itemBean.expandStatus == ExpandStatus.Normal) Painters.File.folder_base else Painters.File.folder_base_open,
                contentDescription = null
            )

        } else {

            val icon = when (itemBean.ext) {
                "kt", "kts" -> Painters.File.kotlin
                "png", "jpg" -> Painters.File.image
                "html", "html5" -> Painters.File.html
                "hex" -> Painters.File.hex
                "jar" -> Painters.File.jar
                "apk" -> Painters.File.android
                "js" -> Painters.File.javascript
                "xml" -> Painters.File.xml
                "txt" -> Painters.File.log
                "md" -> Painters.File.readme
                "gitignore" -> Painters.File.git
                "gradle" -> Painters.File.gradle
                "pdf" -> Painters.File.pdf
                "php" -> Painters.File.php
                "r" -> Painters.File.r
                "java" -> Painters.File.java
                "lua" -> Painters.File.lua
                "c" -> Painters.File.c
                "cpp" -> Painters.File.cpp
                "h" -> Painters.File.h
                "hpp" -> Painters.File.hpp
                "json" -> Painters.File.json
                "go" -> Painters.File.go
                "db" -> Painters.File.database
                "dart" -> Painters.File.dart
                "sh" -> Painters.File.console
                "svg" -> Painters.File.svg
                "vue" -> Painters.File.vue
                "ts" -> Painters.File.typescript
                "cmake" -> Painters.File.cmake
                "zip", "tar", "gz", "tgz" -> Painters.File.zip
                "py" -> Painters.File.python
                "mp4", "mkv" -> Painters.File.video
                "mp3", "wav" -> Painters.File.audio
                "doc", "docx" -> Painters.File.word
                "properties" -> Painters.File.kotlin
                "bat" -> Painters.File.exe
                else -> Painters.File.table
            }
            Image(icon, contentDescription = null)
        }
    }
