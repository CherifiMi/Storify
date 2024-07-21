package core.model

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import kotlinx.serialization.Serializable


@Serializable
data class Item(
    @SerializedName("id")
    val id: String = ObjectId().toString(),
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("wholePrice")
    val wholePrice: Double,
    @SerializedName("sellingPrice")
    val sellingPrice: Double,
    @SerializedName("profit")
    val profit: Double,
    @SerializedName("expirationDate")
    val expirationDate: String,
    @SerializedName("image")
    val image: ByteArray? = null
)

object GsonService {
    private val gson = Gson()

    fun jsonStringToItem(jsonString: String): Item {
        return gson.fromJson(jsonString, Item::class.java)
    }

    fun itemTOJson(item: Item): String {
        return gson.toJson(item)
    }
}