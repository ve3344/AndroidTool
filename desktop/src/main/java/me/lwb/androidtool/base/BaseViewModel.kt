package me.lwb.androidtool.base

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.androidtool.ui.widget.SimpleDialogState

/**
 * Created by ve3344 .
 */


abstract class BaseViewModel {
    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()

                showFailDialog(e.message ?: "")
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                showFailDialog(e.message ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                showFailDialog(e.message ?: e.toString())
            }
        }
    }


    val currentDevice get() = AdbManager.currentDevice


    fun showSucceedDialog(title: String, message: String = "") {
        GlobalUi.setSimpleDialog(SimpleDialogState.Succeed(title, message))
    }

    fun showLoadingDialog(title: String, message: String = "") {
        GlobalUi.setSimpleDialog(SimpleDialogState.Loading(title, message))
    }

    fun showFailDialog(title: String, message: String = "") {
        GlobalUi.setSimpleDialog(SimpleDialogState.Fail(title, message))
    }

    fun hideDialog() {
        GlobalUi.setSimpleDialog(SimpleDialogState.None)
    }


}