package storify

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import domain.model.Item
import domain.model.ItemImage
import core.util.ext.flip
import core.util.ext.update
import core.model.Strings
import data.MongoDBService
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bson.types.ObjectId


@Serializable
data class AppState(
    val items: List<Item> = listOf(),
    val showAddItem: Boolean = false,
    val filer: String = "+Name",
    val searchText: String = "",
    val theme: String = "dark",//"light",//dark
    val lang: String = "en",//"en",//ar
    val calc: String = "single",//whole
    val grid: String = "table",//grid
    val image: ImageBitmap? = null,
    val selectedItem: Item? = null,
    val showSplashScreen: Boolean = true,
)

sealed class AppEvent {
    data class AddItem(val item: Item) : AppEvent()
    data class ShowAddItem(val set: Boolean) : AppEvent()
    data class SetFilter(val filter: String) : AppEvent()
    data class UpdateSearchText(val txt: String) : AppEvent()
    data object FlipTheme : AppEvent()
    data object FlipLang : AppEvent()
    data object FlipGrid : AppEvent()
    data object FlipCalc : AppEvent()
    data class EditItem(val item: Item) : AppEvent()
    data class PlusItem(val item: Item) : AppEvent()
    data class MinItem(val item: Item) : AppEvent()
}


class MainViewModel {

    private val _state = mutableStateOf(AppState())
    val state: State<AppState> = _state

    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val db = MongoDBService

    init {
        viewModelScope.launch {
            db.getItems().let {
                delay(1000)
                _state.update { copy(items = it, showSplashScreen = false) }
            }
        }
    }

    fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.AddItem -> {
                viewModelScope.launch {
                    if (event.item._id.isEmpty()) {
                        val image = state.value.image
                        val itemImage = ItemImage(_id = ObjectId().toString(), image = image)
                        db.insertItem(
                            event.item.copy(
                                _id = ObjectId().toString(),
                                image_id = itemImage._id
                            )
                        )
                        viewModelScope.launch {
                            db.insertImage(itemImage)
                        }
                    } else {
                        db.replaceItem(event.item)
                    }
                    db.getItems().let {
                        _state.update { copy(items = it, selectedItem = null) }
                    }
                }
            }
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

            is AppEvent.EditItem -> {//
                _state.update { copy(selectedItem = event.item, showAddItem = true) }
            }

            is AppEvent.PlusItem -> {
                viewModelScope.launch {
                    db.replaceItem(event.item.copy(quantity = event.item.quantity.plus(1))).let {
                        db.getItems().let {
                            _state.update { copy(items = it) }
                        }
                    }
                }
            }
            is AppEvent.MinItem -> {
                viewModelScope.launch {
                    db.replaceItem(event.item.copy(quantity = event.item.quantity.minus(1))).let {
                        db.getItems().let {
                            _state.update { copy(items = it) }
                        }
                    }
                }
            }
        }

    }

    fun updateImage(toComposeImageBitmap: ImageBitmap) {
        _state.update { copy(image = toComposeImageBitmap) }
    }

    fun drawImage(id: String, f:(image: ImageBitmap?)->Unit) {
        id.ifEmpty { return }
        viewModelScope.launch {
            db.getImage(id)?.let { image ->
                f(image.image)
            }
        }
    }
}

