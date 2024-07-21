import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.di.appModule
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import storify.MainViewModel
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

fun main() = application {
    GlobalContext.startKoin { modules(appModule) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "storify",
    ) {
        App()
    }
}


