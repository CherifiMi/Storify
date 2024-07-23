@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.model.SerializationService
import core.theme.RPTSTheme
import data.Strings.localized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import storify.components.AddItemDialog
import storify.components.ItemGrid
import storify.components.ItemTable
import storify.components.SearchBar
import storify.components.SideBar
import storify.components.TotalCards
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_cubebox


@Composable
expect fun ImagePicker(viewModel: MainViewModel)
expect fun ImageBitmap?.convert(): ByteArray?
expect fun ByteArray.byteArrayToImageBitmap(): ImageBitmap?

@Composable
fun SplashScreen() {
    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.padding(vertical = 240.dp).size(48.dp),
            tint = MaterialTheme.colors.primary,
            painter = painterResource(Res.drawable.ic_cubebox),
            contentDescription = null
        )
    }
}

@Composable
fun AppDebug(viewModel: MainViewModel = koinInject()) {

    val state = viewModel.state.value

    val scop = rememberCoroutineScope()
    var txt by remember { mutableStateOf("running") }
    Column {
        Text(txt)
    }

    val client = OkHttpClient()

    scop.launch {
        try {
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

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

                val jsonResponse = Json.parseToJsonElement(response.body?.string() ?: "").jsonObject
                val documents = jsonResponse["documents"]?.jsonArray ?: emptyList()
                val doc = documents.map { SerializationService.jsonStringToItem(it.toString()) }


                txt =  doc.toString()
            }
        } catch (e: Exception) {
            txt = e.toString()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    val layoutDirection = if (state.lang == "ar") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        RPTSTheme {
            Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                SideBar()
                Column(Modifier.fillMaxSize()) {
                    SearchBar(show = false)
                    TotalCards()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "All items".localized,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onBackground
                        )
                        Box(
                            modifier = Modifier.padding(top = 16.dp).fillMaxSize()
                                .background(MaterialTheme.colors.surface).padding(16.dp)
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = SpaceBetween
                            ) {
                                if (state.grid == "grid") {
                                    ItemGrid()
                                } else {
                                    ItemTable()
                                }
                            }
                        }
                    }

                }
            }
            if (state.showAddItem) {
                AddItemDialog(
                    onDismiss = { viewModel.onEvent(AppEvent.ShowAddItem(false)) },
                    onSave = { item ->
                        viewModel.onEvent(AppEvent.AddItem(item))
                    }
                )
            }
            if (state.showSplashScreen) {
                SplashScreen()
            }

        }
    }
}

