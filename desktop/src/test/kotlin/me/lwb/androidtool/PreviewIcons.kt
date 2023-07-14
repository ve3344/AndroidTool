package me.lwb.androidtool

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import compose.icons.AllIcons
import compose.icons.FeatherIcons

/**
 * Created by ve3344 .
 */
fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {
    var name by remember { mutableStateOf("") }

    Row {
        val chunks = FeatherIcons.AllIcons.chunked(10)
        LazyColumn {
            items(chunks) { chunk ->
                Row {
                    for (icon in chunk) {
                        Image(icon, "", Modifier.clickable {
                            name = icon.name
                        })
                    }
                }

            }
        }
        Text(name)
    }


}
//编写svg图片，实现一个可爱的android logo