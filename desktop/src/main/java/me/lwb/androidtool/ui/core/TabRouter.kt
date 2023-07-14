package me.lwb.androidtool.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.ServiceLoader

/**
 * Created by ve3344 .
 */
data class TabItem(
    val title: String,
    val icon: ImageVector,
    val priority: Int,
    val page: @Composable () -> Unit,
)

interface TabPageHolder {
    val tabItem: TabItem
}

object TabRouter {
    val tabs: List<TabItem> = ServiceLoader.load(TabPageHolder::class.java)
        .map { it.tabItem }
        .sortedBy { it.priority }


}