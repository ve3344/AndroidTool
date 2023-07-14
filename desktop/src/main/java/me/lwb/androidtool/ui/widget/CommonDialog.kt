package me.lwb.androidtool.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.rememberDialogState

/**
 * Created by ve3344 .
 */
@Composable
fun CommonDialog(
    onCloseRequest: () -> Unit,
    title: String,
    icon: Painter?=null,
    state: DialogState = rememberDialogState(),
    content: @Composable DialogWindowScope.() -> Unit,
) {
    Dialog(onCloseRequest = onCloseRequest, undecorated = true, transparent = true, state = state) {
        WindowBar(title, icon, false, false, onCloseRequest = onCloseRequest,onChangeAlwaysOnTopRequest={}, onMinimizedRequest = {}) {
            content()
        }
    }
}