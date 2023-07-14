package me.lwb.androidtool.library.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import me.lwb.androidtool.data.bean.snapshot
import java.util.LinkedList
import java.util.UUID

/**
 * Created by ve3344 .
 */
open class ComposeStackGroup {
    private var composables: LinkedList<ComposableWrapper> =
        LinkedList<ComposableWrapper>()

    private val stack = mutableStateOf(composables.snapshot())

    fun push(key: String = newDialogKey(), composable: @Composable (RemoveHandle) -> Unit): String {
        remove(key)
        composables += ComposableWrapper(key, composable)
        stack.value = composables.snapshot()

        return key
    }

    fun pop(): Boolean {
        if (composables.isEmpty()) {
            return false
        }
        composables.removeLast()
        stack.value = composables.snapshot()
        return true
    }

    fun remove(key: String): Boolean {
        val iterator = composables.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.key == key) {
                iterator.remove()
                stack.value = composables.snapshot()
                return true
            }
        }
        return false
    }

    @Composable
    fun compose() {
        if (stack.value.isEmpty()) {
            return
        }
        for (item in stack.value) {
            item.onCompose()
        }

    }

    fun interface RemoveHandle {
        fun remove()
    }

   inner class ComposableWrapper(val key: String, val composable: @Composable (RemoveHandle) -> Unit):RemoveHandle{
       override fun remove() {
           remove(key)
       }
       @Composable
       fun onCompose(){
           composable(this)
       }
   }



    companion object {
        private fun newDialogKey() = UUID.randomUUID().toString()
    }
}