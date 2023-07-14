package me.lwb.androidtool.library.loadmore


/**
 * Created by ve3344@qq.com.
 * 数据加载类
 */
fun interface LoadMoreDataFetcher<T, P : LoadMoreProgress> {
    suspend fun fetch(progress: P): Collection<T>?
}
