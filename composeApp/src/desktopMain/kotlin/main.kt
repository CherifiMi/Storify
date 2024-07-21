import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.di.appModule
import org.koin.core.context.GlobalContext

fun main() = application {
    GlobalContext.startKoin { modules(appModule) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "storify",
    ) {
        App()
    }
}

