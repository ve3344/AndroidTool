package me.lwb.androidtool.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Package
import compose.icons.feathericons.Upload
import kotlinx.coroutines.launch
import me.lwb.androidtool.LocalWindow
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.common.utils.md5
import me.lwb.androidtool.common.utils.sha1
import me.lwb.androidtool.common.utils.sha256
import me.lwb.androidtool.data.bean.ApkBean
import me.lwb.androidtool.library.compose.decodeComposeImageBitmap
import me.lwb.androidtool.library.compose.onDrop
import me.lwb.androidtool.library.platform.swing.SwingUtils
import me.lwb.androidtool.library.platform.swing.files
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.CommonPopup
import me.lwb.androidtool.vm.DeviceViewModel
import java.awt.dnd.DnDConstants
import java.io.File

@AutoService(TabPageHolder::class)
class ApkPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("安装包", FeatherIcons.Package, 3) { ApkPage() }
}

/**
 * Created by ve3344 .
 */
@Composable
fun ApkPage(viewModel: DeviceViewModel = rememberViewModel()) {

    val files by viewModel.apkFiles.collectAsState()

    ApkInfo(files, {
        viewModel.addApks(it)
    }, {
        viewModel.delete(it)
    }) {
        viewModel.installApk(it)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ApkInfo(
    list: List<ApkBean>,
    addApks: (List<File>) -> Unit,
    deleteApk: (ApkBean) -> Unit,
    install: (File) -> Unit,
) {
    if (list.isEmpty()) {

        val scope = rememberCoroutineScope()
        Column(
            Modifier.clickable {
                scope.launch {
                    val file = SwingUtils.chooseFile {} ?: return@launch
                    addApks(listOf(file))
                }
            }
                .fillMaxSize()
                .onDrop(LocalWindow.current, true) { event ->
                    event.acceptDrop(DnDConstants.ACTION_REFERENCE)
                    addApks(event.files())
                    event.dropComplete(true)
                }
                .background(ThemeColors.GreyBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(FeatherIcons.Upload, "", tint = ThemeColors.GreyText)
            Text(
                "拖动Apk到此处",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 10.dp),
                color = ThemeColors.GreyText
            )
        }
        return
    }


    Row(Modifier.onDrop(LocalWindow.current, true) { event ->
        event.acceptDrop(DnDConstants.ACTION_REFERENCE)
        addApks(event.files())
        event.dropComplete(true)
    }) {
        var selectItem: ApkBean? by remember { mutableStateOf(null) }
        val selected = remember(selectItem,list) {
            selectItem?:list.getOrNull(0)
        }
        var showDelete by remember { mutableStateOf(false) }

        LazyColumn(modifier = Modifier.width(200.dp).background(ThemeColors.GreyBackground)) {
            itemsIndexed(list) { index, item ->


                Column {
                    if (showDelete&&selected === item) {
                        CommonPopup({ showDelete = false }) {
                            Box(Modifier.size(180.dp, 40.dp).clickable {
                                deleteApk(item)
                                showDelete = false
                            }, contentAlignment = Alignment.Center) {
                                Text("删除", textAlign = TextAlign.Center)

                            }

                        }
                    }
                    val file = item.file
                    val icon = item.apkInfo.allIcons.first().data.decodeComposeImageBitmap()
                    val meta = item.apkInfo.apkMeta

                    val bg = if (selected === item)
                        Modifier.background(Color.LightGray, RectangleShape) else Modifier
                    Row(
                        modifier = Modifier.clickable { selectItem = item }
                            .onClick(
                                true,
                                matcher = PointerMatcher.mouse(PointerButton.Secondary)
                            ) {
                                selectItem = item
                                showDelete = true
                            }
                            .then(bg)

                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(icon, "", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                meta.label,
                                fontSize = 14.sp,
                                color = ThemeColors.DarkText,
                                fontWeight = FontWeight.Medium
                            )
                            Text(file.path, fontSize = 12.sp, color = ThemeColors.GreyText)
                        }

                    }
                }


            }

        }

        selected?:return
        val file = selected.file
        val meta = selected.apkInfo.apkMeta
        val apkSign = selected.apkSign
        val icon = selected.apkInfo.allIcons.first().data.decodeComposeImageBitmap()

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(icon, "", modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(10.dp))
            Text(meta.label, fontSize = 20.sp, color = ThemeColors.DarkText)
            Spacer(Modifier.height(10.dp))



            ApkInfoItem("版本", "V${meta.versionName}(${meta.versionCode})")
            ApkInfoItem("包名", meta.packageName)
            ApkInfoItem("最低API", meta.minSdkVersion)
            ApkInfoItem("目标API", meta.targetSdkVersion)
            ApkInfoItem("编译API", meta.compileSdkVersion)
            ApkInfoItem("权限", meta.permissions.size.toString())
            ApkInfoItem("签名MD5", apkSign?.md5())
            ApkInfoItem("签名SHA1", apkSign?.sha1())
            ApkInfoItem("签名SHA256", apkSign?.sha256())

            Spacer(Modifier.weight(1f))
            Button({
                install(file)
            }, modifier = Modifier) {
                Text("安装")
            }
        }

    }

}

@Composable
fun ApkInfoItem(key: String, value: String?, modifier: Modifier = Modifier) {
    val cb = LocalClipboardManager.current
    Row(modifier = modifier) {
        Text(
            text = key,
            maxLines = 1,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.background(ThemeColors.ThemeMain).padding(6.dp).width(50.dp)
        )
        Text(
            text = value ?: "",
            maxLines = 1,
            fontSize = 10.sp,
            color = ThemeColors.DarkText,
            modifier = Modifier.clickable {
                cb.setText(AnnotatedString(value ?: ""))
            }.padding(6.dp).fillMaxWidth()
        )
    }

}

