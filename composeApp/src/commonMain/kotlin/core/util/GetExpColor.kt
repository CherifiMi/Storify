package core.util

import androidx.compose.ui.graphics.Color
import core.theme.Green
import core.theme.Red
import core.theme.Yellow

fun String.getExpColor(): Color {
    val exp_level = getExpirationLevel(this)

    return when (exp_level) {
        0 -> Color.Gray.copy(alpha = .7f)
        1 -> Green
        2 -> Yellow
        3 -> Red
        else -> Color.Gray.copy(alpha = .7f)
    }
}