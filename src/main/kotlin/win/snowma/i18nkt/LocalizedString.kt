package win.snowma.i18nkt

import kotlin.reflect.KProperty

interface LocalizedString: LocalizableField {
    var text: String?

    fun content(): String

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String

    override fun toString(): String
}

internal class LocalizedStringImpl(val default: String? = null): LocalizedString {
    override var text: String? = null

    override fun content() = this.text ?: this.default ?: "null"

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return this.content()
    }

    override fun toString(): String {
        return "LocalizedString(default=$default)"
    }
}

fun localizedString(default: String? = null): LocalizedString {
    return LocalizedStringImpl(default)
}

fun String.args(vararg args: Any): String {
    var result = this
    args.forEachIndexed { index, any -> result = result.replace("{$index}", any.toString()) }
    return result
}