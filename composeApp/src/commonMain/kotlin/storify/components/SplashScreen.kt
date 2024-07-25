package storify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_cubebox

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