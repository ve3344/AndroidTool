package me.lwb.androidtool.base

import me.lwb.androidtool.library.loadmore.LoadMoreData
import me.lwb.androidtool.library.loadmore.LoadMoreDataFetcher
import me.lwb.androidtool.library.loadmore.PageProgress

/**
 * Created by ve3344 .
 */
fun <T:Any> BaseViewModel.pagerOf(fetcher: LoadMoreDataFetcher<T, PageProgress>? = null) =
    LoadMoreData(viewModelScope,true, PageProgress(), fetcher = fetcher)