@file:OptIn(ExperimentalFoundationApi::class)

package me.lwb.androidtool.ui.widget

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import compose.icons.FeatherIcons
import compose.icons.feathericons.Lock
import compose.icons.feathericons.Minimize2
import me.lwb.androidtool.ui.core.GlobalUi
import me.lwb.androidtool.ui.theme.ThemeColors
import kotlin.math.roundToInt

/**
 * Created by ve3344 .
 */


@Composable
fun WindowScope.WindowBar(
    title: String, icon: Painter?,
    hasAlwaysOnTop: Boolean = true,
    hasMinimized: Boolean = true,
    onChangeAlwaysOnTopRequest:(Boolean)->Unit,
    onMinimizedRequest:()->Unit,
    onCloseRequest:()->Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(6.dp),
        elevation = 4.dp
    ) {
        Column {
            WindowDraggableArea(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.height(36.dp), verticalAlignment = Alignment.CenterVertically) {
                    icon?.let {
                        Image(it, "", Modifier.padding(10.dp))
                    }
                    Text(
                        title,
                        modifier = Modifier.padding(start = 10.dp).weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    var windowLock by remember { mutableStateOf(false) }
                    val bg = remember(windowLock) {
                        onChangeAlwaysOnTopRequest(windowLock)
                        if (windowLock) ThemeColors.GreyBackground else Color.Transparent
                    }
                    if (hasAlwaysOnTop) {
                        TooltipArea({ GlobalUi.showToast("置顶") }) {
                            Icon(FeatherIcons.Lock, "置顶", modifier = Modifier.clickable {
                                windowLock = !windowLock
                            }.background(bg).padding(10.dp))
                        }
                    }
                    if (hasMinimized) {

                        TooltipArea({ GlobalUi.showToast("最小化") }) {

                            Icon(FeatherIcons.Minimize2, "最小化", modifier = Modifier.clickable {
                                onMinimizedRequest()
                            }.padding(10.dp))
                        }
                    }

                    TooltipArea({ GlobalUi.showToast("关闭") }) {
                        Icon(Icons.Default.Close, "关闭", modifier = Modifier.clickable {
                            onCloseRequest()
                        }.padding(10.dp))
                    }
                }
            }

            Divider(color = Color.LightGray)
            content()
        }
    }

}
@Composable
fun PopDialogBarLayout(
    title: String="", icon: Painter?=null,
    onCloseRequest:()->Unit={},
    modifier: Modifier=Modifier,
    content: @Composable () -> Unit,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }

    var bounds = remember { Rect(0f,0f,0f,0f) }
    var parentSize = remember { IntSize(0,0) }
    Surface(
        modifier = Modifier.offset {
            IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
        }.onPlaced {
            parentSize=it.parentLayoutCoordinates?.size?:IntSize(0,0)
            bounds=(it.boundsInParent())
        },
        shape = RoundedCornerShape(6.dp),
        elevation = 4.dp
    ) {
        Column(modifier=modifier) {
            Box(modifier = Modifier.fillMaxWidth().onDrag {
                val left=(bounds.left+it.x).coerceIn(0f,parentSize.width-bounds.width)
                val top=(bounds.top+it.y).coerceIn(0f,parentSize.height-bounds.height)

                offset += Offset(left-bounds.left,top-bounds.top)
            }) {
                Row(Modifier.height(36.dp), verticalAlignment = Alignment.CenterVertically) {
                    icon?.let {
                        Image(it, "", Modifier.padding(10.dp))
                    }
                    Text(
                        title,
                        modifier = Modifier.padding(start = 10.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.weight(1f))

                    TooltipArea({ GlobalUi.showToast("关闭") }) {
                        Icon(Icons.Default.Close, "关闭", modifier = Modifier.clickable {
                            onCloseRequest()
                        }.padding(10.dp))
                    }
                }
            }

            Divider(color = Color.LightGray)
            content()
        }
    }

}

