package core.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem.Companion.SYSTEM_TEMPORARY_DIRECTORY
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.MainViewModel
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_box

@Composable
fun MongoImage(
    modifier: Modifier = Modifier,
    image_url: String?
) {
    image_url?.let { url ->
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
        )
    } ?: Image(
        modifier = modifier,
        painter = painterResource(Res.drawable.ic_box),
        contentDescription = null
    )
}

