package domain.model

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class ItemImage(
    val _id: String = ObjectId().toString(),
    @Serializable(with = ImageBitmapSerializer::class)
    val image: ImageBitmap? = null
)