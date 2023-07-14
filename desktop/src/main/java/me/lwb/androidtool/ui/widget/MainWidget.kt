package me.lwb.androidtool.ui.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.lwb.androidtool.ui.core.TabRouter
import me.lwb.androidtool.ui.page.ConnectDevicePage
import me.lwb.logger.Logger


@Composable
fun MainWidget() {
    val pageHolder = rememberSaveableStateHolder()

    ConnectDevicePage()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(Modifier.weight(1f)) {
            ContentWidget(pageHolder)
        }

        BottomBar()

    }

}



@Preview
@Composable
fun ContentWidget(pageHolder: SaveableStateHolder) {

    val tabs = TabRouter.tabs
    Column {
        var tabIndex by remember { mutableStateOf(0) }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            val tabItem = tabs[tabIndex]
            pageHolder.SaveableStateProvider(tabItem.title) {
                tabItem.page()
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        ScrollableTabRow(tabIndex, modifier = Modifier, backgroundColor = Color.Transparent) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(tab.title) },
                    icon = {
                        Icon(tab.icon, "")
                    },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
    }


}

