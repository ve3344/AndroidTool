package me.lwb.androidtool.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Settings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.data.local.SettingItem
import me.lwb.androidtool.library.platform.swing.SwingUtils
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.vm.SettingViewModel
import javax.swing.JFileChooser

/**
 * Created by ve3344 .
 */
@AutoService(TabPageHolder::class)
class SettingPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("设置", FeatherIcons.Settings, 20) { SettingPage() }
}

@Composable
fun SettingPage(
    viewModel: SettingViewModel = rememberViewModel(),
) {

    SideEffect {
        viewModel.loadSetting()
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        FileChooseSetting(viewModel.adbPath) {
            viewModel.updateAdbPath(it)
        }
        FileChooseSetting(viewModel.exportAppDir, true) {
            viewModel.updateExportAppDir(it)
        }
        Spacer(Modifier.height(10.dp))


        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button({ viewModel.saveSetting() }) {
                Text("保存")
            }
        }

    }
}

@Composable
fun FileChooseSetting(flow: StateFlow<SettingItem>, dir: Boolean = false, onSet: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 40.dp).padding(10.dp, 2.dp)
    ) {
        val settingItem by flow.collectAsState()

        TextField(
            settingItem.value, { onSet(it) },
            label = {
                Text(
                    settingItem.title,
                    color = if (settingItem.dirty) ThemeColors.DirtyText else ThemeColors.DarkText
                )
            },
            modifier = Modifier.weight(1f)
        )

        val scope = rememberCoroutineScope()

        Button(
            {
                scope.launch {
                    val choose = SwingUtils.chooseFile {
                        dialogTitle = "选择${settingItem.title}"
                        if (dir) {
                            setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        } else {
                            setFileSelectionMode(JFileChooser.FILES_ONLY);
                        }
                    } ?: return@launch
                    onSet(choose.absolutePath)

                }
            }
        ) {
            Text("选择")
        }
    }
}
