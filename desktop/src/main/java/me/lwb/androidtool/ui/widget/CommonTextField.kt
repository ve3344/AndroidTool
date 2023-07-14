package me.lwb.androidtool.ui.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.lwb.androidtool.ui.theme.ThemeColors

/**
 * Created by ve3344 .
 */
@Composable
fun CommonTextField(value:String,onValue:(String)->Unit,modifier: Modifier=Modifier){
    val source = rememberSaveable() { MutableInteractionSource() }
    val focus by source.collectIsFocusedAsState()
    BasicTextField(
        value,onValue,
        Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .defaultMinSize(100.dp, 48.dp)
            .border(
                if (focus) 2.dp else 1.dp,
                if (focus) ThemeColors.ThemeMain else ThemeColors.GreyText,
                RoundedCornerShape(2.dp)
            ).then(modifier),
        interactionSource = source
    )

}