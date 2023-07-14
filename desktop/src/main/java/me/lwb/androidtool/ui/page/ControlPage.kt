package me.lwb.androidtool.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.google.auto.service.AutoService
import compose.icons.FeatherIcons
import compose.icons.feathericons.Codepen
import compose.icons.feathericons.Inbox
import me.lwb.androidtool.base.rememberViewModel
import me.lwb.androidtool.data.bean.ControlConfigBean
import me.lwb.androidtool.library.platform.android.KeyMap
import me.lwb.androidtool.ui.core.TabItem
import me.lwb.androidtool.ui.core.TabPageHolder
import me.lwb.androidtool.ui.theme.ThemeColors
import me.lwb.androidtool.ui.widget.CommonPopup
import me.lwb.androidtool.ui.widget.CommonTextField
import me.lwb.androidtool.vm.ControlViewModel

/**
 * Created by ve3344 .
 */
@AutoService(TabPageHolder::class)
class ControlPageTabHolder : TabPageHolder {
    override val tabItem = TabItem("控制", FeatherIcons.Codepen, 2) { ControlPage() }
}

@Composable
fun ControlPage(
    viewModel: ControlViewModel = rememberViewModel(),
) {
    val configBean by viewModel.controlConfig.collectAsState()
    val group = configBean ?: return
    LazyColumn {
        items(group.children) {
            ConfigBeanItem(viewModel, it, 0)
        }
    }
}

@Composable
fun ScreenCap(viewModel: ControlViewModel, title: String) {
    val screenshot by viewModel.screenshot.collectAsState()

    Column {
        Button({
            viewModel.screenshot()
        }) {
            Text(title)
        }

        val img = screenshot
        if (img != null) {
            val width = 400.dp
            val height = width * (img.height / img.width)
            Dialog(onCloseRequest = {
                viewModel.clearScreenshot()
            }, title = "屏幕截图", state = rememberDialogState(width = width, height = height)) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(img, "", alignment = Alignment.Center)
                }
            }
        }
    }

}


@Composable
private fun ConfigBeanItem(viewModel: ControlViewModel, it: ControlConfigBean, level: Int = 0) {
    when (it) {
        is ControlConfigBean.Input -> {
            InputAction(it) { value ->
                if (value.isNotEmpty()){
                    viewModel.shell(it.action.replace("<input>", value))
                }
            }
        }

        is ControlConfigBean.Button -> {
            Button({
                viewModel.shell(it.action)
            }) {
                Text(it.title)
            }
        }

        is ControlConfigBean.Keyboard -> {
            KeyAction(viewModel, it.title)
        }

        is ControlConfigBean.ScreenCapture -> {
            ScreenCap(viewModel, it.title)
        }

        is ControlConfigBean.Group -> {
            Column(
                Modifier.padding((level * 10).dp, 10.dp, 2.dp)
                    .border(
                        2.dp, Color(0xffbababa),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                if (it.title.isNotEmpty()) {
                    Text(
                        it.title,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .background(ThemeColors.ThemeSub, RoundedCornerShape(2.dp))
                            .padding(6.dp, 4.dp),
                        color = ThemeColors.LightText
                    )
                }

                Column(Modifier.padding(4.dp)) {
                    if (it.spanCount <= 1) {
                        it.children.forEach {
                            ConfigBeanItem(viewModel, it, level + 1)
                        }
                    } else {
                        it.children.chunked(it.spanCount).forEach {
                            Row(Modifier.fillMaxWidth()) {
                                it.forEach {
                                    Box(
                                        Modifier.weight(1f).align(Alignment.CenterVertically),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ConfigBeanItem(viewModel, it, level + 1)
                                    }
                                }
                            }
                        }

                    }

                }

            }

        }

    }

}

@Composable
private fun KeyAction(viewModel: ControlViewModel, title: String) {
    var keyLog by remember { mutableStateOf("") }
    var kbControl by remember { mutableStateOf(false) }
    Column(modifier = Modifier.onKeyEvent { key ->
        if (key.type == KeyEventType.KeyUp) {
            KeyMap.get(key.key)?.let {
                viewModel.shell("input keyevent $it")

                keyLog = getTrimKeyLog(keyLog, key)
            }
        }
        kbControl
    }) {

        AnimatedVisibility(kbControl){
            CommonPopup(onDismissRequest = { kbControl = false }) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)
                ) {
                    Icon(FeatherIcons.Inbox, "")
                    Spacer(Modifier.height(10.dp))

                    Box(Modifier.defaultMinSize(100.dp,40.dp), contentAlignment = Alignment.Center,) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            val alphaStep = 1f / keyLog.length
                            var alpha = alphaStep
                            for (c in keyLog) {
                                Text(
                                    c.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.background(
                                        ThemeColors.GreyBackground,
                                        RoundedCornerShape(2.dp)
                                    ).alpha(alpha)
                                )
                                alpha += alphaStep
                            }
                        }
                    }
                    Button({
                        kbControl = false
                    }) {
                        Text("结束")
                    }
                }

            }
        }

        Button({
            kbControl = !kbControl
        }) {
            Text(title)
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun getTrimKeyLog(showText: String, it: KeyEvent): String {
    val str = when {
        it.key == Key.Enter -> "Enter"
        it.key == Key.Backspace -> "⇤"
        it.key == Key.DirectionLeft -> "←"
        it.key == Key.DirectionRight -> "→"
        it.key == Key.DirectionUp -> "↑"
        it.key == Key.DirectionDown -> "↓"
        else -> it.utf16CodePoint.toChar().toString()
    }
    return if (showText.length > 5) {
        showText.substring(1, showText.length) + str
    } else {
        showText + str
    }
}

@Composable
private fun InputAction(configBean: ControlConfigBean.Input, action: (String) -> Unit) {
    Row(Modifier.height(IntrinsicSize.Min).fillMaxWidth()) {
        var inputValue: String by rememberSaveable() { mutableStateOf("") }

        CommonTextField(
            inputValue, { inputValue = it },
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        )
        Button({
            action(inputValue)
        }) {
            Text(configBean.title)
        }

    }
}
