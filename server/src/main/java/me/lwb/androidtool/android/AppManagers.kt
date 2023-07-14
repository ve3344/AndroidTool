package me.lwb.androidtool.android

import me.lwb.androidtool.android.service.ClipboardManagerLike

/**
 * Created by ve3344 .
 */
object AppManagers {

    val clipboardManager by lazy { ClipboardManagerLike(FakeApp.PACKAGE_NAME,FakeApp.USER_ID) }
}