package domain.model

import kotlinx.serialization.Serializable
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
    val image_id: String? = null
)