package win.snowma.i18nkt

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.util.Locale



interface ILocalizable<T> {

    val directory: File

    fun locale(locale: Locale): T

    fun locale(): Locale

    interface Child
}

abstract class Localizable<T : Any>(private var locale: Locale = Locale.getDefault()): ILocalizable<T> {
    final override fun locale(): Locale = this.locale

    @Suppress("UNCHECKED_CAST")
    override fun locale(locale: Locale): T {
        return getOrNew(this::class.java, locale) as T
    }

    companion object {
        private val instances = mutableMapOf<Class<*>, MutableMap<Locale, ILocalizable<*>>>()
        private val lock = Any()

        internal fun <T: Localizable<*>> load(instance: T) {
            if(!instance.directory.exists()) { instance.directory.mkdirs() }
            val file = instance.directory.resolve("${instance.locale()}.json")
            val jsonObject = if(file.exists()) {
                Json.decodeFromString<JsonObject>(file.readText())
            } else {
                val json = Json { prettyPrint = true }
                JsonObject(instance.asJsonElements()).also { file.writeText(json.encodeToString(it)) }
            }
            injectFrom(jsonObject, instance)
        }

        @Suppress("UNCHECKED_CAST")
        internal fun <T: Localizable<*>> getOrNew(localizable: Class<T>, locale: Locale): T {
            val instance = synchronized(lock) {
                val map = this.instances.getOrPut(localizable) { mutableMapOf() }
                map.getOrPut(locale) {
                    localizable.newInstance().apply {
                        this.locale = locale
                        load(this)
                    } }
            }
            return instance as T
        }
    }
}