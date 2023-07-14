package me.lwb.androidtool.library.platform.android

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import java.awt.event.KeyEvent

/**
 * Created by ve3344 .
 */
@OptIn(ExperimentalComposeUiApi::class)
object KeyMap {
    private val map: MutableMap<Key, Int> = HashMap()

    fun get(key: Key): Int? {
        return map[key]?:getOther(key.nativeKeyCode)
    }
    private fun getOther(code: Int): Int? {
        when(code) {
            KeyEvent.VK_WINDOWS -> return AndroidKeyCode.KEYCODE_HOME
            KeyEvent.VK_CONTEXT_MENU -> return AndroidKeyCode.KEYCODE_MENU
            KeyEvent.VK_SPACE -> return AndroidKeyCode.KEYCODE_SPACE
            KeyEvent.VK_CAPS_LOCK -> return AndroidKeyCode.KEYCODE_CAPS_LOCK
            KeyEvent.VK_TAB -> return AndroidKeyCode.KEYCODE_TAB
            KeyEvent.VK_ESCAPE -> return AndroidKeyCode.KEYCODE_BACK
            KeyEvent.VK_PRINTSCREEN -> return AndroidKeyCode.KEYCODE_SYSRQ
            KeyEvent.VK_NUM_LOCK -> return AndroidKeyCode.KEYCODE_NUM_LOCK
            KeyEvent.VK_PAUSE -> return AndroidKeyCode.KEYCODE_MEDIA_PLAY_PAUSE
            KeyEvent.VK_INSERT -> return AndroidKeyCode.KEYCODE_INSERT
            KeyEvent.VK_HOME -> return AndroidKeyCode.KEYCODE_MOVE_HOME
            KeyEvent.VK_PAGE_UP -> return AndroidKeyCode.KEYCODE_PAGE_UP
            KeyEvent.VK_DELETE -> return AndroidKeyCode.KEYCODE_DEL
            KeyEvent.VK_END -> return AndroidKeyCode.KEYCODE_MOVE_END
            KeyEvent.VK_PAGE_DOWN -> return AndroidKeyCode.KEYCODE_PAGE_DOWN
            KeyEvent.VK_BACK_SPACE -> return AndroidKeyCode.KEYCODE_DEL
            KeyEvent.VK_ENTER -> return AndroidKeyCode.KEYCODE_DPAD_CENTER
            KeyEvent.VK_LEFT -> return AndroidKeyCode.KEYCODE_DPAD_LEFT
            KeyEvent.VK_UP -> return AndroidKeyCode.KEYCODE_DPAD_UP
            KeyEvent.VK_RIGHT -> return AndroidKeyCode.KEYCODE_DPAD_RIGHT
            KeyEvent.VK_DOWN -> return AndroidKeyCode.KEYCODE_DPAD_DOWN
            KeyEvent.VK_MINUS -> return AndroidKeyCode.KEYCODE_MINUS
            KeyEvent.VK_EQUALS -> return AndroidKeyCode.KEYCODE_EQUALS
            KeyEvent.VK_OPEN_BRACKET -> return AndroidKeyCode.KEYCODE_LEFT_BRACKET
            KeyEvent.VK_CLOSE_BRACKET -> return AndroidKeyCode.KEYCODE_RIGHT_BRACKET
            KeyEvent.VK_BACK_SLASH -> return AndroidKeyCode.KEYCODE_BACKSLASH
            KeyEvent.VK_SEMICOLON -> return AndroidKeyCode.KEYCODE_SEMICOLON
            KeyEvent.VK_COMMA -> return AndroidKeyCode.KEYCODE_COMMA
            KeyEvent.VK_PERIOD -> return AndroidKeyCode.KEYCODE_PERIOD
            KeyEvent.VK_SLASH -> return AndroidKeyCode.KEYCODE_SLASH
            KeyEvent.VK_BACK_QUOTE -> return AndroidKeyCode.KEYCODE_GRAVE
            KeyEvent.VK_QUOTE -> return AndroidKeyCode.KEYCODE_APOSTROPHE
            else -> return null
        }
    }
    init {

        (Key.A.nativeKeyCode - AndroidKeyCode.KEYCODE_A).let { offset ->
            (Key.A.nativeKeyCode..Key.Z.nativeKeyCode).forEach {
                map[Key(it)] = it - offset
            }
        }
        (Key.Zero.nativeKeyCode - AndroidKeyCode.KEYCODE_0).let { offset ->
            (Key.Zero.nativeKeyCode..Key.Nine.nativeKeyCode).forEach {
                map[Key(it)] = it - offset
            }
        }
        (Key.F1.nativeKeyCode - AndroidKeyCode.KEYCODE_F1).let { offset ->
            (Key.F1.nativeKeyCode..Key.F12.nativeKeyCode).forEach {
                map[Key(it)] = it - offset
            }
        }
        map[Key.Backspace] = AndroidKeyCode.KEYCODE_DEL
        map[Key.Escape] = AndroidKeyCode.KEYCODE_ESCAPE
        map[Key.Enter] = AndroidKeyCode.KEYCODE_ENTER
        map[Key.Spacebar] = AndroidKeyCode.KEYCODE_SPACE
        map[Key.Tab] = AndroidKeyCode.KEYCODE_TAB
        map[Key.CtrlLeft] = AndroidKeyCode.KEYCODE_CTRL_LEFT
        map[Key.CtrlRight] = AndroidKeyCode.KEYCODE_CTRL_RIGHT

    }
}