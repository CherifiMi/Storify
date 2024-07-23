package core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId


@Serializable
data class Item(
    val _id: String = ObjectId().toString(),
    val name: String,
    val quantity: Int,
    val wholePrice: Double,
    val sellingPrice: Double,
    val profit: Double,
    val expirationDate: String,
    val image: ByteArray? = null
)

object SerializationService {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun jsonStringToItem(jsonString: String): Item {
        return json.decodeFromString(jsonString)
    }

    fun itemTOJson(item: Item): String {
        return json.encodeToString(item)
    }
}