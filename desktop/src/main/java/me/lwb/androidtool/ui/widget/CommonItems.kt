package me.lwb.androidtool.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.lwb.jsonrpc.RpcCallException
import me.lwb.androidtool.library.loadmore.LoadMoreStatus
import me.lwb.androidtool.library.loadmore.ComposeLoadMoreData
import me.lwb.androidtool.library.loadmore.items
import me.lwb.androidtool.ui.theme.ThemeColors

@Suppress("UNUSED_VARIABLE")
fun <T : Any> LazyListScope.commonItems(
    loadMoreData: ComposeLoadMoreData<T>,
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {
    items(loadMoreData, itemContent)
    when (val status= loadMoreData.loadStatus) {
        is LoadMoreStatus.Fail -> {
            item {

                val message = when (val e = status.throwable) {
                    is IllegalStateException, is IllegalArgumentException -> e.message
                    is RpcCallException -> e.message
                    else -> e.toString()
                }
                Box(
                    Modifier.fillMaxWidth().height(30.dp).clickable { loadMoreData.retry() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "" + message,
                        color = ThemeColors.ErrorText,
                        fontSize = 12.sp
                    )
                }
            }
        }
        is LoadMoreStatus.Idle -> {


        }
        is LoadMoreStatus.Loading -> {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = ThemeColors.GreyText)
                }
            }
        }
        is LoadMoreStatus.NoMore -> {
            item {
                Box(Modifier.fillMaxWidth().height(30.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "没有更多了",
                        color = ThemeColors.GreyText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
