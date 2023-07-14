package me.lwb.androidtool.utils

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * Created by ve3344 .
 */
interface IObjectReflector : IClassReflector {
    val delegateObject: Any
    override val delegateClass: Class<*> get() = delegateObject.javaClass
}

interface IClassReflector {
    val delegateClass: Class<*>
}

interface VarargMethod<R> {
    val methodName: String
    operator fun invoke(vararg args: Any?): R
}

private class VarargMethodImpl<R>(
    val method: Method,
    val delegateObject: Any?,
    val returnType: Class<R>,
) : VarargMethod<R> {
    override val methodName: String get() = method.name
    override fun invoke(vararg args: Any?): R = if (args.isNotEmpty()) {
         method.invoke(delegateObject, *(args.unwrap())).wrap(returnType)
     } else {
         method.invoke(delegateObject).wrap(returnType)
     }
}
private class VarargConstructorImpl<R>(
    val method: Constructor<*>,
    val returnType: Class<R>,
) : VarargMethod<R> {
    override val methodName: String get() = method.name
    override fun invoke(vararg args: Any?): R = if (args.isNotEmpty()) {
         method.newInstance(*(args.unwrap())).wrap(returnType)
     } else {
         method.newInstance().wrap(returnType)
     }
}

fun classReflector(clz: Class<*>) = object : IClassReflector {
    override val delegateClass: Class<*> = clz
}

fun classReflector(clz: String) = classReflector(Class.forName(clz))


private fun Array<*>.unwrap() = map {
    (it as? IObjectReflector)?.delegateObject ?: it
}.toTypedArray()

private fun <T> T?.unwrap(): T? {
    return (this as? IObjectReflector)?.delegateObject as? T ?: this
}


private fun <R> Any?.wrap(clz: Class<*>): R {
    return if (IObjectReflector::class.java.isAssignableFrom(clz)) {
        clz.getDeclaredConstructor(Any::class.java).newInstance(this) as R
    } else {
        this as R
    }
}


class LazyField<R>(
    private val clz: Class<*>,
    private val obj: Any?,
    private val fieldName: String?,
    private val fieldType: Class<R>,
) : ReadWriteProperty<Any?, R> {
    private var cacheField: Field? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): R {
        val field = cacheField ?: findField(fieldName ?: property.name)
        field.isAccessible = true
        return field.get(obj)?.wrap(fieldType) as R
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
        val field = cacheField ?: findField(fieldName ?: property.name)
        field.isAccessible = true
        field.set(obj, value.unwrap())
    }

    private fun findField(name: String): Field {
        return clz.andSupers().mapNotNull {
            try {
                it.getDeclaredField(name)
            } catch (e: NoSuchFieldException) {
                null
            }
        }.firstOrNull() ?: throw NoSuchFieldException(name)
    }


}

fun Class<*>.andSupers() = sequence<Class<*>> {
    var cur = this@andSupers
    while (true) {
        yield(cur)

        val superclass = cur.superclass
        if (superclass == null || superclass == Object::class.java) {
            break
        }
        cur = superclass
    }
}

fun Array<*>.unwrapParams() = map {
    when (it) {
        is Class<*> -> it.also { c -> check(!IClassReflector::class.java.isAssignableFrom(c)) { "Can not be a class of ClassReflector" } }
        is KClass<*> -> it.java.also { c -> check(!IClassReflector::class.java.isAssignableFrom(c)) { "Can not be a class of ClassReflector" } }
        is String -> Class.forName(it)
        is IClassReflector -> it.delegateClass
        else -> throw IllegalStateException("Invalid $it")
    }
}.toTypedArray()

class LazyMethod<R>(
    val wrapper: IObjectReflector,
    val params: Array<out Any>,
    val returnType: Class<R>,
) : ReadOnlyProperty<Any?, VarargMethod<R>> {
    var overrideName: String? = null
    private var varargMethod: VarargMethod<R>? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        varargMethod ?: newVarargMethod(overrideName ?: property.name)

    private fun newVarargMethod(name: String): VarargMethod<R> {
        val par = params.unwrapParams()
        val method = wrapper.delegateClass.getDeclaredMethod(name, *par)
        method.isAccessible = true
        return VarargMethodImpl(method, wrapper.delegateObject,returnType)
    }


    inline fun <reified R> override(params: Array<out Any> = this.params) =
        LazyMethod<R>(wrapper, params, R::class.java)
}

class LazyStaticMethod<R>(
    val wrapper: IClassReflector,
    val isConstructor: Boolean,
    val params: Array<out Any>,
    val returnType: Class<R>,
) : ReadOnlyProperty<Any?, VarargMethod<R>> {
    var overrideName: String? = null
    private var varargMethod: VarargMethod<R>? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        varargMethod ?: newVarargMethod(overrideName ?: property.name)


    private fun newVarargMethod(name: String): VarargMethod<R> {
        val par = params.unwrapParams()
        if (isConstructor) {
            val method = wrapper.delegateClass.getDeclaredConstructor(*par)
            method.isAccessible = true
            return VarargConstructorImpl(method,returnType)
        } else {
            val method = wrapper.delegateClass.getDeclaredMethod(name, *par)
            method.isAccessible = true
            return VarargMethodImpl(method,null,returnType)
        }


    }
}

fun <R> LazyStaticMethod<R>.overrideOf(origin: VarargMethod<*>) = apply {
    overrideName = origin.methodName
}

fun <R> LazyMethod<R>.overrideOf(origin: VarargMethod<*>) = apply {
    overrideName = origin.methodName
}
fun <R> LazyStaticMethod<R>.overrideOf(origin: KProperty0<VarargMethod<*>>) = apply {
    overrideName = origin.name
}

fun <R> LazyMethod<R>.overrideOf(origin: KProperty0<VarargMethod<*>>) = apply {
    overrideName = origin.name
}

fun <R> LazyStaticMethod<R>.overrideOf(methodName: String) = apply {
    overrideName = methodName
}

fun <R> LazyMethod<R>.overrideOf(methodName: String) = apply {
    overrideName = methodName
}

inline fun <reified R> IObjectReflector.field(name: String? = null): LazyField<R> =
    LazyField<R>(this.delegateClass, this.delegateObject, name, R::class.java)

inline fun <reified R> IClassReflector.staticField(name: String? = null): LazyField<R> =
    LazyField<R>(this.delegateClass, null, name, R::class.java)

inline fun <reified R> IObjectReflector.method(vararg params: Any): LazyMethod<R> =
    LazyMethod<R>(this, params, R::class.java)

inline fun <reified R> IClassReflector.staticMethod(vararg params: Any): LazyStaticMethod<R> =
    LazyStaticMethod<R>(this, false, params, R::class.java)

inline fun <reified R> IClassReflector.construct(vararg params: Any): LazyStaticMethod<R> =
    LazyStaticMethod<R>(this, true, params, R::class.java)


class FileIObjectReflector(override val delegateObject: Any) : IObjectReflector {
    val append by method<StringBuilder>(Int::class)
    var count by field<Int>()

    companion object : IClassReflector {
        override val delegateClass: Class<*> = StringBuilder::class.java
        val constructor by construct<StringBuilder>(String::class)

    }
}


fun main() {

    println(StringBuilder::class.java.andSupers().toList())
    val f = FileIObjectReflector.constructor("a.t")
    FileIObjectReflector(f).append(1)
//    println(FileIObjectReflector(f).getPath())
    println(f.toString())
    println(FileIObjectReflector(f).count)
}