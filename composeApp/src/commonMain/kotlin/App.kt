@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import core.model.Strings.localized
import core.theme.RPTSTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
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
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_box
import storify.saveAppState


@Composable
expect fun ImagePicker(viewModel: MainViewModel)
expect fun ImageBitmap.convert(): ByteArray
expect fun ByteArray.byteArrayToImageBitmap(): ImageBitmap?
expect fun getFilePath(fileName: String): String


@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun App2() = RPTSTheme {


    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        var isAnimate by remember { mutableStateOf(false) }
        val transition = rememberInfiniteTransition()
        val rotate by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing)
            )
        )

        AsyncImage(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp)
                .run { if (isAnimate) rotate(rotate) else this },
            model = "https://media.themoviedb.org/t/p/w440_and_h660_face/czembW0Rk1Ke7lCJGahbOhdCuhV.jpg",
            contentDescription = null
        )

        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .widthIn(min = 200.dp),
            onClick = { isAnimate = !isAnimate },
            content = {
                Icon(vectorResource(Res.drawable.ic_box), contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    if (isAnimate) "Stop" else "Run"
                )
            }
        )
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context).memoryCachePolicy(CachePolicy.ENABLED).memoryCache {
        MemoryCache.Builder().maxSizePercent(context, 0.3).strongReferencesEnabled(true).build()
    }.diskCachePolicy(CachePolicy.ENABLED).networkCachePolicy(CachePolicy.ENABLED).diskCache {
        newDiskCache()
    }.crossfade(true).logger(DebugLogger()).build()

fun newDiskCache(): DiskCache {
    return DiskCache.Builder().directory(okio.FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024) // 512MB
        .build()
}


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
