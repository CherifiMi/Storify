package storify

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import core.model.Item
import core.util.flip
import core.util.update
import data.Strings
import domain.MongoDBService
import kotlinx.coroutines.launch


@Serializable
data class AppState(
    val items: List<Item> = listOf(),
    val showAddItem: Boolean = false,
    val filer: String = "+Name",
    val searchText: String = "",

    val theme: String = "light",//dark
    val lang: String = "en",//ar
    val calc: String = "single",//whole
    val grid: String = "table",//grid


    val image: ImageBitmap? = null,
)

sealed class AppEvent {
    data class AddItem(val item: Item) : AppEvent()
    data object GetAllItems : AppEvent()
    data class ShowAddItem(val set: Boolean) : AppEvent()
    data class SetFilter(val filter: String) : AppEvent()
    data class UpdateSearchText(val txt: String) : AppEvent()

    data object FlipTheme : AppEvent()
    data object FlipLang : AppEvent()
    data object FlipGrid : AppEvent()
    data object FlipCalc : AppEvent()
}



class MainViewModel {
    private val _state = mutableStateOf(AppState())
    val state: State<AppState> = _state
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val db = MongoDBService

    init {
        viewModelScope.launch {
            db.getItems().let {
                _state.update { copy(items = it) }
            }
        }
    }

    fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.AddItem -> {
                viewModelScope.launch {
                    db.insertItem(event.item)
                    db.getItems().let {
                        _state.update { copy(items = it) }
                    }
                }
            }

            AppEvent.GetAllItems -> TODO()
            is AppEvent.ShowAddItem -> {
                _state.update { copy(showAddItem = event.set) }
            }

            is AppEvent.SetFilter -> {

                val currentFilter = state.value.filer

                val firstChar = currentFilter.substring(0, 1)
                val restOfString = currentFilter.substring(1)

                val filter =
                    if (event.filter == restOfString) firstChar.flip() + event.filter else "+" + event.filter

                _state.update { copy(filer = filter) }
            }

            is AppEvent.UpdateSearchText -> _state.update { copy(searchText = event.txt) }
            AppEvent.FlipCalc -> _state.update { copy(calc = if (state.value.calc == "whole") "single" else "whole") }
            AppEvent.FlipGrid -> _state.update { copy(grid = if (state.value.grid == "grid") "table" else "grid") }
            AppEvent.FlipLang -> {
                _state.update { copy(lang = if (state.value.lang == "ar") "en" else "ar") }
                Strings.setLanguage(state.value.lang)
            }
            AppEvent.FlipTheme -> _state.update { copy(theme = if (state.value.theme == "dark") "light" else "dark") }
        }
    }

    fun updateImage(toComposeImageBitmap: ImageBitmap) {
        _state.update { copy(image = toComposeImageBitmap) }
    }
}

