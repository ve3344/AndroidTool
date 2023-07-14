package me.lwb.androidtool.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Info
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.vm.DeviceViewModel

/**
 * Created by ve3344 .
 */
@AutoService(TabPageHolder::class)
class InfoPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("信息", FeatherIcons.Info,0) { InfoPage() }
}

@Composable
fun InfoPage(
    viewModel: DeviceViewModel = rememberViewModel(),
) {
    val prop by viewModel.currentDeviceProp.collectAsState()
    val showConfig by viewModel.showInfoConfig.collectAsState()
    val showInfo = remember(prop,showConfig) {
        showConfig.map {
            val value=prop[it.key]?:""
            val title: String =if (it.title.isNullOrEmpty()){it.key?:""} else it.title?:""

            title to value
        }
    }

    LazyColumn {
        showInfo.forEachIndexed { index, item ->
            showInfoItem(index, item.first, item.second)
        }

    }
}

private fun LazyListScope.showInfoItem(
    index: Int,
    title: String,
    value: String?,
) {
    value ?: return
    val colors = arrayOf(Color(0xFFfafafa), Color(0xFFf2f2f2))
    item {
        val cb = LocalClipboardManager.current
        Row(Modifier.clickable { cb.setText(AnnotatedString(value)) }
            .background(colors[index % 2])
            .padding(top = 2.dp, bottom = 2.dp)

        ) {
            Text(title, Modifier.width(120.dp).padding(start = 10.dp))
            Text(value,
                Modifier.fillMaxWidth().padding(end = 10.dp),
                textAlign = TextAlign.End)
        }
    }
}



