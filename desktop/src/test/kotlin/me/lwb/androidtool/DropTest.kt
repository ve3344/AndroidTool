package me.lwb.androidtool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import me.lwb.androidtool.library.compose.SimpleDropTarget
import me.lwb.androidtool.library.compose.onDrop


/**
 * Created by ve3344 .
 */
fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {
    var name by remember { mutableStateOf("") }

    Row(Modifier.padding(20.dp)) {
        LazyColumn {
            items(20) { i ->
                Text(" $i",
                    Modifier
                    .width(200.dp)
                    .height(30.dp)
                    .background(Color.LightGray)
                    .onDrop(window, true,SimpleDropTarget(onDragOver = {
                        println("onDropOver1 $i")
                    }, onDragEnter = {

                    }, onDragExit = {

                    },
                    onDrop = {
                        name = "$i  " + it.transferable
                        println("Drop $name")
                    }
                        )) )
                Spacer(Modifier.height(1.dp))
            }
        }
        Text(name)
    }


}