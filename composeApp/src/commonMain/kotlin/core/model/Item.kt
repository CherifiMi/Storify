package core.model

import androidx.compose.ui.graphics.ImageBitmap
import byteArrayToImageBitmap
import convert
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
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
    @Serializable(with = ImageBitmapSerializer::class)
    val image: ImageBitmap? = null
)

object SerializationService {
    val json = Json {
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
object ImageBitmapSerializer : KSerializer<ImageBitmap?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ImageBitmap")

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ImageBitmap?) {
        val byteArray = value.convert()
        encoder.encodeNullableSerializableValue(ByteArraySerializer(), byteArray)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ImageBitmap? {
        val byteArray = decoder.decodeNullableSerializableValue(ByteArraySerializer())
        return byteArray?.byteArrayToImageBitmap()
    }
}
