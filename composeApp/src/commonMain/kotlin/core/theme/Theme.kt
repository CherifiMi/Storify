package core.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import org.koin.compose.koinInject
import storify.MainViewModel

// Define your color palette
private val LightColors = lightColors(
    primary = darkGreen,
    secondary = lightGreen,
    background = backWhite,
    surface = Color.White,

    onBackground = Color.Black,
)

private val DarkColors = darkColors(
    primary = lightGreen,
    secondary = darkGreen,
    background = backBlack,
    surface = black,

    onBackground = Color.White
)

@Composable
fun RPTSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    viewModel: MainViewModel = koinInject(),
    content: @Composable () -> Unit
) {
    val state = viewModel.state.value

    val colors = if (state.theme == "dark")  DarkColors else LightColors
    MaterialTheme(
        colors = colors,
        //typography = Typography,
        //shapes = Shapes,
        content = content
    )
}
