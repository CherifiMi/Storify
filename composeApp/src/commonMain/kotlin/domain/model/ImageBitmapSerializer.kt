package domain.model

import androidx.compose.ui.graphics.ImageBitmap
import byteArrayToImageBitmap
import convert
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


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
