package me.lwb.androidtool.library.loadmore

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by ve3344 .
 */

class ComposeLoadMoreData<T : Any>(private val source: LoadMoreDataSource<T>) {
    private val mutableData: MutableList<T> = ArrayList()


    var data: List<T> by mutableStateOf(ImmutableListWrapper(mutableData))
        private set
    var loadStatus: LoadMoreStatus by mutableStateOf(LoadMoreStatus.Idle)
        private set
    val itemCount: Int get() = data.size

    private val uiReceiver get() = source.uiReceiver
    operator fun get(index: Int): T {
        uiReceiver.onItemAccess(index)
        return data[index]
    }


    fun peek(index: Int): T = data[index]


    fun retry() {
        uiReceiver.retry()
    }


    fun reload() {
        uiReceiver.reload()
    }

    internal suspend fun collectLoadMoreStatus() {
        source.statusFlow.collectLatest {
            loadStatus = it
        }
    }

    internal suspend fun collectLoadMoreData() {
        source.dataFlow.collectLatest {
            when(it){
                is DataChangeEvent.Append ->  mutableData.addAll(it.data)
                is DataChangeEvent.Replace -> {
                    mutableData.clear()
                    mutableData.addAll(it.data)
                }
            }
            data = ImmutableListWrapper(mutableData)
        }
    }
}

@Composable
fun <T : Any> LoadMoreDataSource<T>.collectComposeLoadMoreData(): ComposeLoadMoreData<T> {
    val composeLoadMoreData = remember(this) { ComposeLoadMoreData(this) }

    LaunchedEffect(composeLoadMoreData) {
        composeLoadMoreData.collectLoadMoreData()
    }
    LaunchedEffect(composeLoadMoreData) {
        composeLoadMoreData.collectLoadMoreStatus()
    }
    return composeLoadMoreData
}


@Suppress("UNUSED_VARIABLE")
fun <T : Any> LazyListScope.items(
    loadMoreData: ComposeLoadMoreData<T>,
    itemContent: @Composable LazyItemScope.(value: T) -> Unit,
) {

    items(loadMoreData.itemCount) { index ->
        itemContent(loadMoreData[index])
    }
}


fun <T : Any> LazyListScope.itemsIndexed(
    loadMoreData: ComposeLoadMoreData<T>,
    itemContent: @Composable LazyItemScope.(index: Int, value: T) -> Unit,
) {
    items(loadMoreData.itemCount) { index ->
        itemContent(index, loadMoreData[index])
    }
}


internal class ImmutableListWrapper<T>(private val delegate: List<T>) : List<T> by delegate {
    override fun toString() = delegate.toString()
}


