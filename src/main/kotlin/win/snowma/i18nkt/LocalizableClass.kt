package win.snowma.i18nkt

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface LocalizableClass<T: ILocalizable.Child>: LocalizableField {

    var instance: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T

    override fun toString(): String
}

class LocalizableClassImpl<T: ILocalizable.Child>(val clazz: KClass<T>): LocalizableClass<T> {

    override var instance: T = clazz.java.getConstructor().newInstance()

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return instance
    }

    override fun toString(): String {
        return "LocalizableClass(clazz=$clazz)"
    }
}

inline fun <reified T: ILocalizable.Child> localizableClass(): LocalizableClass<T> {
    return LocalizableClassImpl(T::class)
}