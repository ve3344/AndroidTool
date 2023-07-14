package me.lwb.androidtool.data.bean

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Created by ve3344 .
 */
//{
//    "title": "Input",
//    "type": "input",
//    "action": "input text <value>"
//  }
enum class ControlConfigActionType {
    INPUT,
    BUTTON,
    SCREENSHOT,
    KEYBOARD,
    GROUP,
}

@Serializable
sealed interface ControlConfigBean {
    val title: String
    @Serializable
    class Keyboard(
        override val title: String = "",
    ) : ControlConfigBean
    @Serializable
    class ScreenCapture(
        override val title: String = "",
    ) : ControlConfigBean
    @Serializable
    class Input(
        override val title: String = "",
        val action: String = "",
    ) : ControlConfigBean
    @Serializable
    class Button(
        override val title: String = "",
        val action: String = "",
    ) : ControlConfigBean
    @Serializable
    class Group(
        override val title: String = "",
        val children: List<ControlConfigBean> = emptyList(),
        val spanCount: Int = 1,
    ) : ControlConfigBean

    companion object {
        fun loadGroup(path: String): Group {
            val array = Json.Default.parseToJsonElement(File(path).readText()).jsonArray
            return Group("自定义", parseArrayElement(array))
        }
    }
}


private fun JsonObject.getString(key: String) = if (containsKey(key)) get(key)?.jsonPrimitive?.content?:"" else ""

private fun parseArrayElement(array: JsonArray) = array.map {
    parseElement(it.jsonObject)
}

private fun parseElement(element: JsonObject): ControlConfigBean {
    val type = ControlConfigActionType.valueOf(element.getString("type").uppercase())

    val title = element.getString("title")
    return when (type) {
        ControlConfigActionType.GROUP -> {
            ControlConfigBean.Group(title,
                parseArrayElement(element["children"]?.jsonArray?: JsonArray(emptyList())),
                element.getString("spanCount").toIntOrNull() ?: 1)
        }
        ControlConfigActionType.INPUT -> ControlConfigBean.Input(title, element.getString("action"))
        ControlConfigActionType.BUTTON -> ControlConfigBean.Button(title,
            element.getString("action"))
        ControlConfigActionType.SCREENSHOT -> ControlConfigBean.ScreenCapture(title)
        ControlConfigActionType.KEYBOARD -> ControlConfigBean.Keyboard(title)
    }
}
