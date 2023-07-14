package me.lwb.androidtool.utils

/**
 * Created by ve3344 .
 */
object Dump {
    @Throws(Exception::class)
    fun dump(any: Any): String? {
        val sb = StringBuilder()
        val aClass: Class<*>
        if (any is Class<*>) {
            aClass = any
            sb.append("Class ").append(aClass.name)
        } else {
            aClass = any.javaClass
            sb.append("Class ").append(aClass.name).append("(")
                .append(Integer.toHexString(any.hashCode())).append(")")
        }
        sb.append(":").append(aClass.superclass)
        sb.append('\n')
        sb.append("Fields:\n")
        for (field in aClass.declaredFields) {
            field.isAccessible = true
            sb.append("\t").append(field).append("=")
            runCatching {
                if (any is Class<*>) {
                    sb.append(field[null])
                } else {
                    sb.append(field[any])
                }
            }
            sb.append('\n')
        }
        sb.append("Methods:\n")
        for (method in aClass.declaredMethods) {
            method.isAccessible = true
            sb.append("\t").append(method)
            sb.append('\n')
        }
        return sb.toString()
    }
}