package storify.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import storify.Strings.localized

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddButton(viewModel: MainViewModel = koinInject()) {
    Card(
        onClick = { viewModel.onEvent(AppEvent.ShowAddItem(true)) },
        Modifier.fillMaxWidth().height(32.dp),
        contentColor = /*MaterialTheme.colors.primary*/MaterialTheme.colors.primary,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("+ add item".localized, fontSize = 12.sp, color = MaterialTheme.colors.primary)
        }
    }
}