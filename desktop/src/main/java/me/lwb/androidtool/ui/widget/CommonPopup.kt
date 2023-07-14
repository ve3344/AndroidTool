package me.lwb.androidtool.ui.widget

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

/**
 * Created by ve3344 .
 */
@Composable
fun CommonPopup(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    scope: @Composable () -> Unit,
) {
    Popup(onDismissRequest = onDismissRequest) {
        Surface(
            elevation = 6.dp,
            shape = RoundedCornerShape(6.dp),
            modifier = modifier.padding(horizontal = 12.dp)
        ) {
            scope()


        }
    }
}

@Composable
fun CommonPopupDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title:String="",
    icon:Painter?=null,
    modifier: Modifier = Modifier,
    scope: @Composable () -> Unit,
) {

    val alpha by animateFloatAsState(
        if (visible) 1f else 0f, animationSpec = tween(
            durationMillis = 500,
            delayMillis = 50,
            easing = LinearOutSlowInEasing
        )
    )
    if (visible) {
        Popup(onDismissRequest = onDismissRequest, alignment = Alignment.Center, focusable = true) {
            Box(
                Modifier.fillMaxSize()
                    .alpha(alpha)
                    .focusable(true)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(20.dp)
            ) {
                PopDialogBarLayout(title,
                    icon,
                    modifier = modifier,
                    onCloseRequest = onDismissRequest) {
                    scope()
                }
            }

        }
    }

}