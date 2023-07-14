package me.lwb.androidtool.library.loadmore

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * 分页加载模块的数据存储类，类似于Pager的作用
 *
 */

class LoadMoreData<T, P : LoadMoreProgress> constructor(
    private val coroutineScope: CoroutineScope,
    var clearOnReloadStart:Boolean=true,
    val progress: P,
    fetcher: LoadMoreDataFetcher<T, P>? = null,
) {
    private var currentLoadJob: Job? = null
    private val cacheData: MutableList<T> = ArrayList()
    private val dataEventFlow = MutableSharedFlow<DataChangeEvent<T>>()
    private val statusFlow = MutableStateFlow<LoadMoreStatus>(LoadMoreStatus.Idle)

    private val uiReceiver: UiReceiver = UiReceiverImpl()

    val dataFlow = flow {
        onDataAccess()
        emit(DataChangeEvent.Replace(cacheData)) //发送当前最新数据
        emitAll(dataEventFlow) //发送的变更的事件
    }


    val source = LoadMoreDataSource(uiReceiver, dataFlow, statusFlow)

    var fetchDistance = 5

    @Deprecated("Use fetchDistance instead", ReplaceWith("fetchDistance"))
    var preloadCount: Int by ::fetchDistance

    private var fetchMore: LoadMoreDataFetcher<T, P>? = fetcher

    /**
     * 重置fetchMore并重新加载，在大多数情况，尤其是在带参数加载是非常有用，
     * 该方法有个副作用，就是无法在onSaveInstance时保存，如果有onSaveInstance保存请求参数需求时就不要使用
     */
    fun suspendReload(fetchMore: LoadMoreDataFetcher<T, P>) {
        this.fetchMore = fetchMore
        reload()
    }

    private var isInitDataLoad = false
    var initDataLoadEnable = fetcher != null
    private fun onDataAccess() {
        if (initDataLoadEnable && !isInitDataLoad) {
            loadMore()
        }
    }

    /**
     * 重新加载数据
     */
    fun reload(): Boolean {
        cancelLoad()
        progress.resetProgress()
        return loadMoreOrReload(true)
    }

    /**
     * 加载下一页
     */
    fun loadMore() {
        if (statusFlow.value is LoadMoreStatus.Idle) {
            loadMoreOrReload(false)
        }
    }

    /**
     * 错误重试
     */
    fun retry() {
        if (statusFlow.value is LoadMoreStatus.Fail) {
            loadMoreOrReload(false)
        }
    }


    /**
     * 取消加载
     */
    private fun cancelLoad() {
        currentLoadJob?.cancel()
        currentLoadJob = null
        statusFlow.value = LoadMoreStatus.Idle
    }


    /**
     * 加载更多/重新加载
     * @param isReload 重新加载
     */
    private fun loadMoreOrReload(isReload: Boolean): Boolean {
        val fetcher = fetchMore ?: return false
        isInitDataLoad = true
        statusFlow.value = LoadMoreStatus.Loading(isReload)

        val clearOnReloadStartLocal=clearOnReloadStart

        currentLoadJob = coroutineScope.launch {
            try {
                if (clearOnReloadStartLocal&&isReload){
                    cacheData.clear()
                    dataEventFlow.emit(DataChangeEvent.Replace(emptyList()))
                }

                val data = fetcher.fetch(progress) ?: emptyList() //调用加载

                if (data.isNotEmpty()) {
                    //数据不为空
                    progress.nextProgress()
                    if (isReload) {
                        //重新加载，替换数据
                        cacheData.clear()
                        cacheData.addAll(data)
                        dataEventFlow.emit(DataChangeEvent.Replace(data))
                    } else {
                        //加载更多，追加数据
                        cacheData.addAll(data)
                        dataEventFlow.emit(DataChangeEvent.Append(data))
                    }
                    //加载完成，恢复空闲状态
                    statusFlow.value = LoadMoreStatus.Idle
                } else {
                    //数据为空，没有更多数据了
                    statusFlow.value = LoadMoreStatus.NoMore(isReload)
                }

            } catch (e: CancellationException) {
                //协程取消,恢复空闲状态
                statusFlow.value = LoadMoreStatus.Idle
                throw e
            } catch (e: Throwable) {
                //加载失败
                statusFlow.value = LoadMoreStatus.Fail(isReload, e)
            }
        }
        return true
    }

    private inner class UiReceiverImpl : UiReceiver {
        override fun reload() {
            this@LoadMoreData.reload()
        }

        override fun cancel() {
            this@LoadMoreData.cancelLoad()
        }

        override fun retry() {
            this@LoadMoreData.retry()
        }

        override fun onItemAccess(index: Int) {
            if (fetchDistance == FETCH_DISTANCE_DISABLE) {
                //禁用下拉加载
                return
            }
            if (index + fetchDistance !in cacheData.indices) {
                //超出范围，尝试加载下一页
                this@LoadMoreData.loadMore()
            }


        }

    }

    companion object {
        const val FETCH_DISTANCE_DISABLE = 0
    }
}


