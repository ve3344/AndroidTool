package me.lwb.androidtool

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.lwb.androidtool.library.loadmore.LoadMoreData
import me.lwb.androidtool.library.loadmore.PageProgress
import me.lwb.androidtool.library.loadmore.collectComposeLoadMoreData
import me.lwb.androidtool.library.loadmore.itemsIndexed

/**
 * Created by ve3344 .
 */
class LoadMoreViewModel {
    val texts = LoadMoreData(CoroutineScope(Dispatchers.IO),true, PageProgress()) {
        onLoad(it)
    }



    private fun onLoad(it: PageProgress): List<String>? {
        val data =
            (0 until 100)
                .chunked(it.pageSize)
                .getOrNull(it.pageIndex - 1)
                ?.map { it.toString() }
        println("Load $it  : $data")
        return data
    }
}

val vm2 = LoadMoreViewModel()

fun main() {

    singleWindowApplication(
        state = WindowState(width = 380.dp, height = 300.dp),
    ) {
        val data = vm2.texts.source.collectComposeLoadMoreData()

        LazyColumn {
            itemsIndexed(data) { index, icon ->
                println("index $index")
                Text(icon)
            }

        }

    }
}