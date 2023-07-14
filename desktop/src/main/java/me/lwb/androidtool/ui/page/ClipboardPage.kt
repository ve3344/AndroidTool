package me.lwb.androidtool.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clipboard
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.vm.ClipboardViewModel

/**
 * Created by ve3344 .
 */
@AutoService(TabPageHolder::class)
class ClipboardPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("剪贴板", FeatherIcons.Clipboard,5) { ClipboardPage() }
}
@Composable
fun ClipboardPage(viewModel: ClipboardViewModel = rememberViewModel()) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(10.dp)) {

        val clipboardHistory by viewModel.clipboardText.collectAsState()

        var currentText: String by rememberSaveable(clipboardHistory) { mutableStateOf(clipboardHistory) }

        TextField(currentText, { currentText = it }, Modifier.weight(1f).fillMaxWidth())

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button({ viewModel.loadClipboard() }) {
                Text("获取")
            }
            Spacer(Modifier.width(10.dp))
            Button({ viewModel.setClipboard(currentText) }) {
                Text("发送")
            }

        }




    }

}