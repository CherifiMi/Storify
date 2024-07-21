package domain

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
import org.bson.types.ObjectId
import java.io.IOException

object MongoDBService {

    private const val apiUrl = "https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action"
    private const val apiKey = "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"

    private val client = OkHttpClient()

    suspend fun insertItem(item: Item) {
        val jsonBody = Json.encodeToString(mapOf(
            "collection" to "items",
            "database" to "storify",
            "dataSource" to "storify1",
            "document" to GsonService.itemTOJson(item)
        ))

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonBody
        )

        val request = Request.Builder()
            .url("$apiUrl/insertOne")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("api-key", apiKey)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Failed to insert item: ${response.code}")
            }
        }
    }

    suspend fun getItems(): List<Item> {
        val jsonBody = Json.encodeToString(mapOf(
            "collection" to "items",
            "database" to "storify",
            "dataSource" to "storify1"
        ))

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

            val jsonResponse = Json.parseToJsonElement(response.body?.string()?:"").jsonObject
            val documents = jsonResponse["documents"]?.jsonArray ?: emptyList()
            documents.map { GsonService.jsonStringToItem(it.toString()) }
        }
    }
}

/*

object MongoDBService3 {

    private val apiUrl = "https://eu-central-1.aws.data.mongodb-api.com/app/data-utvnrfx/endpoint/data/v1/action"
    private val apiKey = "Q0Uk5NInIcjqnurtinXYTlEAQYb6eUBkCf0yXh7vRyBxLUAgPxf6eEqA1sKRk9YG"

    fun insertItem(item: Item) {
        val url = URL("$apiUrl/insertOne")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("api-key", apiKey)
            doOutput = true

            val jsonPayload = Json.encodeToString(mapOf(
                "collection" to "items",
                "database" to "storify",
                "dataSource" to "storify1",
                "document" to item
            ))

            OutputStreamWriter(outputStream).use {
                it.write(jsonPayload)
            }

            val responseCode = responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed to insert item: $responseCode")
            }
        }
    }

    fun getItems(): List<Item> {
        val url = URL("$apiUrl/find")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("api-key", apiKey)
            doOutput = true

            val jsonPayload = Json.encodeToString(mapOf(
                "collection" to "items",
                "database" to "storify",
                "dataSource" to "storify1"
            ))

            OutputStreamWriter(outputStream).use {
                it.write(jsonPayload)
            }

            val responseCode = responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed to fetch items: $responseCode")
            }

            inputStream.bufferedReader().use {
                val response = it.readText()
                // Assuming the response contains a list of documents
                val jsonResponse = Json.parseToJsonElement(response).jsonObject
                val documents = jsonResponse["documents"]?.jsonArray ?: emptyList()
                return documents.map { Json.decodeFromJsonElement<Item>(it) }
            }
        }
    }
}


object MongoDBService2 {

    val uri = "mongodb+srv://storify0001:Cherifi2003%40@storify1.jfgssq7.mongodb.net/?retryWrites=true&w=majority&appName=storify1"



    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .retryWrites(true)
        .build()
    val mongoClient = MongoClient.create(settings)
    val database = mongoClient.getDatabase("storify")
    val collection = database.getCollection<Item>("items")

    suspend fun insertItem(item: Item) = withContext(Dispatchers.IO) {
        collection.insertOne(item)
    }

    suspend fun getItems(): List<Item> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }
}*/