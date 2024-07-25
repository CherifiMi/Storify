package domain.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SerializationService {
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun jsonStringToItem(jsonString: String): Item {
        return json.decodeFromString(jsonString)
    }
    fun jsonStringToImage(jsonString: String): ItemImage {
        return json.decodeFromString(jsonString)
    }

    fun itemTOJson(item: Item): String {
        return json.encodeToString(item)
    }
}