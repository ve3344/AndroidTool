package me.lwb.androidtool.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import compose.icons.FeatherIcons
import compose.icons.feathericons.Aperture
import me.lwb.androidtool.ui.theme.ThemeColors

/**
 * Created by ve3344 .
 *
 */

sealed interface SimpleDialogState {
    val message: String
    val title: String

    object None : SimpleDialogState {
        override val title: String = ""
        override val message: String = ""
    }

    class Loading(override val title: String, override val message: String) : SimpleDialogState
    class Succeed(override val title: String, override val message: String) : SimpleDialogState
    class Fail(override val title: String, override val message: String) : SimpleDialogState

}

@Composable
fun ToastWidget(message: String) {

    ToastWidget0(message)


}

@Composable
fun ToastWidget0(message: String) {
    Popup(alignment = Alignment.BottomCenter, offset = IntOffset(0, -20)) {
        AnimatedVisibility(message.isNotEmpty()) {

            Surface(
                shape = RoundedCornerShape(5),
                color = ThemeColors.ToastBackground,
                modifier = Modifier.padding(40.dp),
                elevation = 0.dp
            ) {
                Text(message, Modifier.padding(20.dp, 8.dp), textAlign = TextAlign.Center)
            }
        }
    }

}


@Composable
fun LoadingDialogWidget(state: SimpleDialogState, onDismissRequest: () -> Unit) {

    if (state==SimpleDialogState.None){
        return
    }

    Popup(alignment = Alignment.Center) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            Modifier.fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { })
                .padding(20.dp, 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                elevation = 6.dp,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)
                        .height(IntrinsicSize.Min)
                ) {
                    if (state is SimpleDialogState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Icon(FeatherIcons.Aperture, "")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(state.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Text(state.message, fontSize = 13.sp, color = ThemeColors.GreyText)
                    }


                    if (state !is SimpleDialogState.Loading) {
                        Button({
                            onDismissRequest()
                        }) {
                            Text("好的")
                        }
                    }

                }

            }
        }

    }

}
