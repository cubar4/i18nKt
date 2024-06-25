package win.snowma.i18nkt

import kotlinx.serialization.json.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


internal fun <T: Any> T.asJsonElements(json: Json = Json { prettyPrint = true }): Map<String, JsonElement> {
    return this::class.declaredMemberProperties.mapNotNull { property ->
        val javaField = property.javaField?.apply { isAccessible = true } ?:return@mapNotNull null
        if(LocalizableClass::class.java.isAssignableFrom(javaField.type)) {
            val localizableClass = javaField.get(this) as LocalizableClass<*>
            return@mapNotNull property.name to JsonObject(localizableClass.instance.asJsonElements(json = json))
        } else if(LocalizedString::class.java.isAssignableFrom(javaField.type)) {
            return@mapNotNull property.name to JsonPrimitive((javaField.get(this) as LocalizedString).content())
        }
        return@mapNotNull null
    }.associate { it }
}

@Suppress("unchecked_cast")
internal fun <T: Any> injectFrom(jsonObject: JsonObject, instance: T, json: Json = Json { prettyPrint = true }): T {
    instance::class.declaredMemberProperties.forEach { property ->
        val javaField = property.javaField?.apply { isAccessible = true } ?:return@forEach
        if(LocalizableClass::class.java.isAssignableFrom(javaField.type)) {
            val localizableClass = javaField.get(instance) as LocalizableClass<ILocalizable.Child>
            val childInstance = localizableClass.instance
            localizableClass.instance = injectFrom((jsonObject[property.name]?:return@forEach).jsonObject, childInstance, json)
        } else if(LocalizedString::class.java.isAssignableFrom(javaField.type)) {
            val localizedString = javaField.get(instance) as LocalizedString
            localizedString.text = jsonObject[property.name]?.jsonPrimitive?.content?:return@forEach
        }
        return@forEach
    }
    return instance
}