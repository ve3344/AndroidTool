package me.lwb.androidtool.ui.core

import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.lwb.androidtool.library.compose.ComposeStackGroup
import me.lwb.androidtool.ui.widget.CommonPopupDialog
import me.lwb.androidtool.ui.widget.LoadingDialogWidget
import me.lwb.androidtool.ui.widget.SimpleDialogState
import me.lwb.androidtool.ui.widget.ToastWidget

/**
 * Created by ve3344 .
 */
object GlobalUi {
    private val uiScope by lazy { CoroutineScope(SupervisorJob()+Dispatchers.Main) }
    private val snackbarHostState = SnackbarHostState()

    private val dialogs = ComposeStackGroup()

    private val loadingDialogState: MutableState<SimpleDialogState> =
        mutableStateOf(SimpleDialogState.None)
    private val toastState: MutableState<String> = mutableStateOf("")

    /**
     * 顶部放置一个弹窗
     */
    fun pushCommonDialog(scope: @Composable () -> Unit) = pushDialog { handle, visible ->
        CommonPopupDialog(visible, { handle.remove() }) {
            scope()
        }
    }

    /**
     * 顶部放置一个弹窗
     */
    fun pushDialog(scope: @Composable (handle: ComposeStackGroup.RemoveHandle, visible: Boolean) -> Unit) =
        dialogs.push {
            var state by remember { mutableStateOf(false) }
            scope(it, state)
            LaunchedEffect(state) {
                state = true
            }
        }

    suspend fun showSnackbar(message: String) {
        snackbarHostState.showSnackbar(message)
    }


    fun setSimpleDialog(state: SimpleDialogState) {
        loadingDialogState.value = state
    }

    fun showToast(message: String) {
        toastState.value = message

        uiScope.launch {
            delay(2000)
            toastState.value = ""
        }
    }

    @Composable
    fun compose() {
        dialogs.compose()
        SnackbarHost(snackbarHostState)
        LoadingDialogWidget(loadingDialogState.value) {
            loadingDialogState.value = SimpleDialogState.None
        }
        ToastWidget(toastState.value)

    }

}