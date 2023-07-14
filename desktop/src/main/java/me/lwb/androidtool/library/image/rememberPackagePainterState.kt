package me.lwb.androidtool.library.image

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.lwb.androidtool.base.AdbManager

@Composable
fun rememberPackagePainterState(packageName: String): State<Painter?> {
    val painterState = remember<MutableState<Painter?>>(packageName) {
        mutableStateOf(null)
    }
    val device = AdbManager.currentDevice.collectAsState()
    LaunchedEffect(packageName, device) {
        withContext(Dispatchers.IO){
            repeat(3) {
                try {
                    painterState.value = ImageLoader.load(packageName)
                    return@withContext
                } catch (e: Exception) {
                    delay(1000)
                }
            }
        }

    }
    return painterState
}