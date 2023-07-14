@file:OptIn(ExperimentalFoundationApi::class)

package me.lwb.androidtool.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.List
import me.lwb.androidtool.Painters
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.common.services.bean.AppPackage
import me.lwb.androidtool.library.compose.onMouseRightClick
import me.lwb.androidtool.library.image.rememberPackagePainterState
import me.lwb.androidtool.library.loadmore.collectComposeLoadMoreData
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.CommonPopupDialog
import me.lwb.androidtool.ui.widget.SimpleDropdownMenuItem
import me.lwb.androidtool.ui.widget.commonItems
import me.lwb.androidtool.vm.AppsViewModel

@AutoService(TabPageHolder::class)
class AppsPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("应用", FeatherIcons.List, 4) { AppsPage() }
}

/**
 * Created by ve3344 .
 */

@Composable
fun AppsPage(
    viewModel: AppsViewModel = rememberViewModel(),
) {


    AppInfo(viewModel)

    AppList(viewModel)
}

@Composable
private fun AppInfo(viewModel: AppsViewModel) {

    val appInfo by viewModel.appDetail.collectAsState()

    val appInfoLocal = appInfo ?: return

    val icon by rememberPackagePainterState(appInfoLocal.packageName)



    LaunchedEffect(appInfoLocal) {
        GlobalUi.pushDialog { handle, visible ->
            CommonPopupDialog(visible, {
                viewModel.clearAppInfo()
                handle.remove()
            }, appInfoLocal.label, icon) {
                val showInfo: List<Pair<String, String>> = remember(appInfoLocal) {
                    appInfoLocal.javaClass.declaredFields.map {
                        it.isAccessible = true
                        it.name to it.get(appInfoLocal).toString()
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
                        itemsIndexed(showInfo) { index, item ->
                            showInfoItem(index, item.first, item.second)
                        }
                    }
                    Spacer(Modifier.width(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Button({
                            viewModel.uninstall(appInfoLocal.packageName)
                        }) {
                            Text("卸载")
                        }
                        Button({
                            viewModel.export(appInfoLocal)
                        }) {
                            Text("导出")
                        }

                        Button({
                            viewModel.launchApp(appInfoLocal.packageName)
                        }) {
                            Text("启动")
                        }
                        Button({
                            viewModel.stopApp(appInfoLocal.packageName)
                        }) {
                            Text("停止")
                        }

                    }
                    Spacer(Modifier.width(2.dp))

                }

            }

        }

    }

}


@Composable
private fun showInfoItem(
    index: Int,
    title: String,
    value: String,
) {
    val colors = arrayOf(Color(0xFFfafafa), Color(0xFFf2f2f2))
    val cb = LocalClipboardManager.current
    Row(Modifier.clickable { cb.setText(AnnotatedString(value)) }
        .background(colors[index % 2])
        .padding(top = 2.dp, bottom = 2.dp)

    ) {
        Text(title, Modifier.width(160.dp).padding(start = 10.dp))
        Text(
            value,
            Modifier.fillMaxWidth().padding(end = 10.dp),
            textAlign = TextAlign.End, fontSize = 12.sp
        )
    }
}


@Composable
private fun AppList(viewModel: AppsViewModel) {

    val appList = viewModel.appPager.source.collectComposeLoadMoreData()

    val currentDevice by viewModel.currentDevice.collectAsState()
    val filterData by viewModel.appFilterData.collectAsState()

    LaunchedEffect(currentDevice,filterData) {
        viewModel.loadAppList()
    }

    Column {

        FilterPanel(viewModel)

        LazyColumn {

            commonItems(appList) {
                Column {
                    var menuOpen by remember { mutableStateOf(false) }
                    AppMenu(
                        menuOpen,
                        { menuOpen = false },
                        it, viewModel
                    )


                    Row(Modifier
                        .clickable { viewModel.loadAppInfo(it.packageName) }
                        .onMouseRightClick { menuOpen=true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppImage(it.packageName)
                        Spacer(Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    it.label,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f),
                                    color = ThemeColors.DarkText,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(it.versionName, fontSize = 12.sp, color = ThemeColors.GreyText)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(it.packageName, fontSize = 13.sp, color = ThemeColors.GreyText)
                        }
                    }

                    Divider(
                        Modifier.height(1.dp).fillMaxWidth(),
                        color = ThemeColors.GreyBackground
                    )

                }

            }
        }
    }


}

@Composable
fun FilterPanel(viewModel: AppsViewModel) {

    var expand by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(color = Color.White, elevation = 2.dp) {
        Column(modifier = Modifier.padding(10.dp, 2.dp)) {
            val filterData by viewModel.appFilterData.collectAsState()

            var filterKey: String by remember(filterData) { mutableStateOf(filterData.searchKey) }
            var showSystem: Boolean by remember(filterData) { mutableStateOf(filterData.showSystem) }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(0.dp,5.dp)) {
                Text(
                    "筛选($filterKey ${if (showSystem) "显示系统应用" else ""})",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = ThemeColors.GreyText
                )

                if (!expand) {

                    Icon(Icons.Default.KeyboardArrowDown, "", tint = ThemeColors.GreyText, modifier = Modifier.clickable {
                        expand = true
                    })
                } else {
                    Button({
                        expand = false
                        viewModel.updateFilterData(filterKey, showSystem)
                    }) {
                        Text("确认")
                    }
                }
            }
            AnimatedVisibility(expand) {

                Divider(color = ThemeColors.Divider)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(filterKey, { filterKey = it }, maxLines = 5, label = { Text("筛选") }, modifier = Modifier.fillMaxWidth())

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(showSystem, { showSystem = it })
                        Spacer(Modifier.width(10.dp))
                        Text("显示系统应用")
                    }
                }

            }

        }

    }


}


@Composable
fun AppImage(packageName: String) {
    val painter by rememberPackagePainterState(packageName)
    val painterLocal = painter
    Box(Modifier.size(30.dp).clip(RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
        if (painterLocal != null) {
            Image(painterLocal, "")
        } else {
            Image(
                Painters.app_package,
                "",
                Modifier.size(30.dp).background(ThemeColors.GreyBackground).padding(4.dp)
            )
        }

    }


}


@Composable
private fun AppMenu(
    menuVisible: Boolean,
    onDismissMenu: () -> Unit,
    bean: AppPackage,
    viewModel: AppsViewModel
) {
    DropdownMenu(
        expanded = menuVisible,
        onDismissRequest = onDismissMenu,
        offset = DpOffset(10.dp, 10.dp),
    ) {
        val clipboardManager = LocalClipboardManager.current
        SimpleDropdownMenuItem("复制包名", {
            clipboardManager.setText(AnnotatedString(bean.packageName))
            GlobalUi.showToast("复制成功")
            onDismissMenu()
        })
        SimpleDropdownMenuItem("复制应用名", {
            clipboardManager.setText(AnnotatedString(bean.label))
            GlobalUi.showToast("复制成功")
            onDismissMenu()
        })
        SimpleDropdownMenuItem("导出APK", {
            viewModel.export(bean)
            onDismissMenu()
        })
        SimpleDropdownMenuItem("启动APP", {
            viewModel.launchApp(bean.packageName)
            onDismissMenu()
        })

    }
}
