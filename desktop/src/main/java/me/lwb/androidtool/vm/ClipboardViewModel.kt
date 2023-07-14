package me.lwb.androidtool.vm

import com.google.auto.service.AutoService
import kotlinx.coroutines.flow.MutableStateFlow
import me.lwb.androidtool.base.BaseViewModel
import me.lwb.androidtool.data.repository.ClipboardRepository
import me.lwb.androidtool.ui.core.GlobalUi

/**
 * Created by ve3344 .
 */
@AutoService(BaseViewModel::class)
open class ClipboardViewModel : BaseViewModel() {
    val repository = ClipboardRepository()

    val clipboardText: MutableStateFlow<String> = MutableStateFlow("")

    fun loadClipboard() {
        launch {
            clipboardText.value = repository.getClipboard()
            GlobalUi.showToast("获取成功")
        }

    }

    fun setClipboard(text: String) {
        if (text.isEmpty()){
            return
        }
        launch {
            repository.setClipboard(text)
            GlobalUi.showToast("发送成功")
        }
    }
}