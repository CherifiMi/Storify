@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import core.model.Strings.localized
import core.theme.RPTSTheme
import core.util.getAsyncImageLoader
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import storify.components.AddItemDialog
import storify.components.EditItemDialog
import storify.components.ItemGrid
import storify.components.ItemTable
import storify.components.SearchBar
import storify.components.SideBar
import storify.components.SplashScreen
import storify.components.TotalCards
import storify.saveAppState


@Composable
expect fun ImagePicker(viewModel: MainViewModel)
expect fun ImageBitmap.convert(): ByteArray
expect fun ByteArray.byteArrayToImageBitmap(): ImageBitmap?
expect fun getFilePath(fileName: String): String
expect suspend fun uploadImageToS3(image: ImageBitmap): String


@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class)
@Composable
@Preview
fun App(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    val layoutDirection = if (state.lang == "ar") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    LaunchedEffect(state){
        println("done")
        saveAppState(state, getFilePath("state.json"))
    }


    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        RPTSTheme {
            setSingletonImageLoaderFactory { context ->
                getAsyncImageLoader(context)
            }

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
                        println("add item")
                        viewModel.onEvent(AppEvent.AddItem(item))
                    }
                )
            }
            if (state.showEditItem) {
                EditItemDialog(
                    onDismiss = { viewModel.onEvent(AppEvent.ShowEditItem(false,)) },
                    onEdit = { item ->
                        viewModel.onEvent(AppEvent.EditItem(item))
                    }
                )
            }
            if (state.showSplashScreen) {
                SplashScreen()
            }
        }
    }
}
