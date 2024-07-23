package domain

import core.model.Item
import core.model.SerializationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object MongoDBService {

    private const val apiUrl =
        "https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action"
    private const val apiKey = "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"

    private val client = OkHttpClient()

    suspend fun insertItem(item: Item) {

        val json = SerializationService.json

        val itemJson = json.encodeToString(item)
        println(itemJson)

        val itemJsonElement = json.encodeToJsonElement(item)

        val jsonObject = buildJsonObject {
            put("collection", JsonPrimitive("items"))
            put("database", JsonPrimitive("storify"))
            put("dataSource", JsonPrimitive("storify1"))
            put("document", itemJsonElement)
        }

        // Convert the JsonObject to a JSON string
        val jsonBody = json.encodeToString(jsonObject)

        println("item json \n $jsonBody")

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

        val requestBody = /*jsonBody.toRequestBody()*/
            jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action/find")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Access-Control-Request-Headers", "*")
            .addHeader(
                "api-key",
                "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"
            )
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Failed to fetch items: ${response.code}")
            }

            val jsonResponse = Json.parseToJsonElement(response.body?.string() ?: "").jsonObject
            val documents = jsonResponse["documents"]?.jsonArray ?: emptyList()
            documents.map { SerializationService.jsonStringToItem(it.toString()) }
        }
    }

    suspend fun replaceItem(item: Item) {


        val json = Json { encodeDefaults = true }

        val itemJsonElement = json.encodeToJsonElement(item)

        val jsonObject = buildJsonObject {
            put("collection", JsonPrimitive("items"))
            put("database", JsonPrimitive("storify"))
            put("dataSource", JsonPrimitive("storify1"))
            put("filter", buildJsonObject { put("_id", JsonPrimitive(item._id)) })
            put("update", buildJsonObject {
                put("\$set", itemJsonElement)
            })
        }

        // Convert the JsonObject to a JSON string
        val jsonBody = json.encodeToString(jsonObject)

        println("Item JSON \n$jsonBody")

        val requestBody = jsonBody.toRequestBody()

        val request = Request.Builder()
            .url("https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action/updateOne")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("api-key", "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG")
            .build()

        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
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

}

