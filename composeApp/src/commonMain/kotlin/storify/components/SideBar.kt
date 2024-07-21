package storify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_calc
import storify.composeapp.generated.resources.ic_cubebox
import storify.composeapp.generated.resources.ic_grid
import storify.composeapp.generated.resources.ic_lang
import storify.composeapp.generated.resources.ic_theme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SideBar(viewModel: MainViewModel = koinInject()) {
    Column(
        Modifier.fillMaxHeight().width(70.dp).background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Icon(
            modifier = Modifier.padding(vertical = 24.dp).size(48.dp),
            tint = MaterialTheme.colors.primary,
            painter = painterResource(Res.drawable.ic_cubebox),
            contentDescription = null
        )


        Card(
            modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
            onClick = { viewModel.onEvent(AppEvent.FlipGrid) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_grid),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
        Card(
            modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
            onClick = { viewModel.onEvent(AppEvent.FlipCalc) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_calc),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
        Card(
            modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
            onClick = { viewModel.onEvent(AppEvent.FlipTheme) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_theme),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
        Card(
            modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
            onClick = { viewModel.onEvent(AppEvent.FlipLang) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_lang),
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}