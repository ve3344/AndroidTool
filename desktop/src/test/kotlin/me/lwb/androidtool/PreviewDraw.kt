package me.lwb.androidtool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

/**
 * Created by ve3344 .
 */
fun main() = singleWindowApplication(
    state = WindowState(width = 380.dp, height = 300.dp),
) {
    var name by remember { mutableStateOf("") }

    Row {
        AndroidLogoScreen()
    }



}

@Composable
fun AndroidLogoScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        CuteAndroidLogo(color = Color.Green)
    }
}

@Composable
fun CuteAndroidLogo(color: Color) {
    Box(
        Modifier
            .size(200.dp)
            .background(Color.White, CircleShape)
    ) {
        // 头部
        Box(
            Modifier
                .size(120.dp)
                .offset(40.dp, -30.dp)
                .background(color, CircleShape)
        )
        // 左耳朵
        Box(
            Modifier
                .size(60.dp)
                .offset(10.dp, -10.dp)
                .background(color, CircleShape)
        )
        // 右耳朵
        Box(
            Modifier
                .size(60.dp)
                .offset(130.dp, -10.dp)
                .background(color, CircleShape)
        )
        // 眼睛
        Box(
            Modifier
                .size(20.dp)
                .offset(60.dp, 50.dp)
                .background(Color.White, CircleShape)
        )
        Box(
            Modifier
                .size(20.dp)
                .offset(120.dp, 50.dp)
                .background(Color.White, CircleShape)
        )
        // 嘴巴
        Box(
            Modifier
                .size(60.dp, 20.dp)
                .offset(70.dp, 80.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
        )
    }
}


//编写svg图片，实现一个Android 机器人 logo