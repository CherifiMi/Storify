import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import storify.MainViewModel


@Composable
expect fun ImagePicker(viewModel: MainViewModel)


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

    ImagePicker(viewModel)

    /*
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
    */
}


