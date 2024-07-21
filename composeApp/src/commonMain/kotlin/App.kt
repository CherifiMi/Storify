import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.theme.RPTSTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import storify.components.AddButton
import storify.components.AddItemDialog
import storify.AppEvent
import storify.AppEvent.ShowAddItem
import storify.components.ItemTable
import storify.MainViewModel
import storify.components.SearchBar
import storify.components.SideBar
import storify.Strings.localized
import storify.components.ItemGrid
import storify.components.TotalCards
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_box


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    val layoutDirection = if (state.lang == "ar") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        RPTSTheme {
            Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                SideBar()
                Column(Modifier.fillMaxSize()) {
                    SearchBar()
                    TotalCards()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "All items".localized, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = MaterialTheme.colors.onBackground)
                        Box(
                            modifier = Modifier.padding(top = 16.dp).fillMaxSize()
                                .background(MaterialTheme.colors.surface).padding(16.dp)
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = SpaceBetween
                            ) {
                                if (state.grid == "grid"){
                                    ItemGrid()
                                }else{
                                    ItemTable()
                                }
                            }
                        }
                    }

                }
            }
            if (state.showAddItem) {
                AddItemDialog(
                    onDismiss = { viewModel.onEvent(ShowAddItem(false)) },
                    onSave = { item ->
                        viewModel.onEvent(AppEvent.AddItem(item))
                    }
                )
            }

        }
    }
}


