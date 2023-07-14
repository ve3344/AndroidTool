@file:Suppress("UNCHECKED_CAST")

package me.lwb.androidtool.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Created by ve3344 .
 */
object ViewModelStore {
    val map: MutableMap<Class<out BaseViewModel>, BaseViewModel> = HashMap()
    fun register(value: BaseViewModel) {
        map[value.javaClass] = value
    }

    operator fun <T : BaseViewModel> get(clz: Class<T>): T {
        return requireNotNull(map[clz] as? T) { "Get viewModel $clz fail" }
    }
}

@Composable
inline fun <reified T : BaseViewModel> rememberViewModel(): T {
    return remember {
        ViewModelStore.get(T::class.java)
    }
}