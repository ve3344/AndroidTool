package me.lwb.androidtool.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KMutableProperty0

fun KMutableProperty0<String>.asSettingStateFlow(title: String, initValue: String = "") =
    MutableSettingStateFlow(
        this,
        MutableStateFlow(SettingItem(title, initValue)),
    )

class MutableSettingStateFlow(
    private val prop: KMutableProperty0<String>,
    private val delegate: MutableStateFlow<SettingItem>,
) :
    MutableStateFlow<SettingItem> by delegate {

    fun update(value: String) {
        delegate.value = delegate.value.copy(value = value, dirty = true)
    }

    suspend fun save() {
        delegate.value = delegate.value.copy(dirty = false)
        prop.set(delegate.value.value)
    }

    suspend fun load() {
        delegate.value = delegate.value.copy(value = prop.get(), dirty = false)
    }
}

data class SettingItem(
    val title: String,
    val value: String,
    val dirty: Boolean = false
)