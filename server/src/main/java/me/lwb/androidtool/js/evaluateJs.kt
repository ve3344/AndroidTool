package me.lwb.androidtool.js

/**
 * Created by ve3344 .
 */
 fun < T>evaluateJs(script:String):T{
   return JsHelper.instance.evaluate(script) as T

}
inline fun <reified T>evaluateJs(script:()->String):T{
   return JsHelper.instance.evaluate(script()) as T
}