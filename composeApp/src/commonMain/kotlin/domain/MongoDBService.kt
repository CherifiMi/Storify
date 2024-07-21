package domain

import com.google.gson.Gson
import core.model.GsonService
import core.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import java.io.IOException

object MongoDBService {

    private const val apiUrl =
        "https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action"
    private const val apiKey = "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"

    private val client = OkHttpClient()

    suspend fun insertItem(item: Item) {
        val gson = Gson()

        val jsonBody = gson.toJson(
            mapOf(
                "collection" to "items",
                "database" to "storify",
                "dataSource" to "storify1",
                "document" to item
            )
        )

        val requestBody = jsonBody.toRequestBody()

        val request = Request.Builder()
            .url("https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action/insertOne")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Access-Control-Request-Headers", "*")
            .addHeader(
                "api-key",
                "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"
            )
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Request Body: $jsonBody")
                    println("Response: ${response.body?.string()}")
                    throw IOException("Unexpected code $response")
                } else {
                    println("Response: ${response.body?.string()}")
                }
            }
        }
    }

    suspend fun getItems(): List<Item> {
        val jsonBody = Json.encodeToString(
            mapOf(
                "collection" to "items",
                "database" to "storify",
                "dataSource" to "storify1"
            )
        )

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonBody
        )

        val request = Request.Builder()
            .url("$apiUrl/find")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("api-key", apiKey)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Failed to fetch items: ${response.code}")
            }

            val jsonResponse = Json.parseToJsonElement(response.body?.string() ?: "").jsonObject
            val documents = jsonResponse["documents"]?.jsonArray ?: emptyList()
            documents.map { GsonService.jsonStringToItem(it.toString()) }
        }
    }
}

