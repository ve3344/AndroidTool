package me.lwb.androidtool

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

/**
 * Created by ve3344 .
 */

fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {

    val stateHolder = rememberSaveableStateHolder()


    val array= arrayOf("A","B")

    Column {
        var selectIndex by remember { mutableStateOf(0) }

        Button({
            selectIndex=(selectIndex+1)%2
        }){
            Text("Select $selectIndex")
        }
        stateHolder.SaveableStateProvider(array[selectIndex]) {
            if (selectIndex == 0) {
                Page1()
            } else {
                Page2()
            }
        }
    }



}

@Composable
fun Page1() {
    Column {
        Text("Page1")
        var value by rememberSaveable() { mutableStateOf("") }
        TextField(value, { value = it })
    }
}

@Composable
fun Page2() {

    Column {
        Text("Page2")
        var value by rememberSaveable { mutableStateOf("") }
        TextField(value, { value = it })
    }
}
