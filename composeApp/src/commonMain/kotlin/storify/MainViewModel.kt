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
import getFilePath
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import uploadImageToS3
import java.io.File

@Serializable
data class AppState(
    @Transient val items: List<Item> = listOf(),
    @Transient val showAddItem: Boolean = false,
    @Transient val showEditItem: Boolean = false,
    @Transient val image: ImageBitmap? = null,
    @Transient val selectedItem: Item? = null,
    @Transient val showSplashScreen: Boolean = true,
    val filer: String = "+Name",
    val searchText: String = "",
    val theme: String = "dark",//"light",//dark
    val lang: String = "en",//"en",//ar
    val calc: String = "single",//whole
    val grid: String = "table",//grid
)


fun saveAppState(state: AppState, filePath: String) {
    try {
        val json = Json.encodeToString(state)

        println(json)
        File(filePath).writeText(json)
    } catch (e: Exception) {
        println(e)
    }
}

fun loadAppState(filePath: String): AppState? {
    return try {
        val json = File(filePath).readText()
        Json.decodeFromString<AppState>(json)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


sealed class AppEvent {
    data class AddItem(val item: Item) : AppEvent()
    data class EditItem(val item: Item) : AppEvent()
    data class ShowAddItem(val set: Boolean) : AppEvent()
    data class ShowEditItem(val set: Boolean, val item: Item? = null) : AppEvent()

    data class SetFilter(val filter: String) : AppEvent()
    data class UpdateSearchText(val txt: String) : AppEvent()

    data object FlipTheme : AppEvent()
    data object FlipLang : AppEvent()
    data object FlipGrid : AppEvent()
    data object FlipCalc : AppEvent()

    data class PlusItem(val item: Item) : AppEvent()
    data class MinItem(val item: Item) : AppEvent()
}


class MainViewModel {

    private val _state = mutableStateOf(AppState())
    val state: State<AppState> = _state

    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val db = MongoDBService


    init {
        loadAppState(getFilePath("state.json")).let {
            _state.update { it ?: AppState() }
            Strings.setLanguage(it?.lang ?: "en")
        }
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
                    val url = state.value.image?.let { uploadImageToS3(it) }

                    db.insertItem(
                        event.item.copy(
                            _id = ObjectId().toString(),
                            image_id = url
                        )
                    ).let {
                        db.getItems().let {
                            _state.update { copy(items = it, selectedItem = null) }
                        }
                    }
                }
            }

            is AppEvent.EditItem -> {
                viewModelScope.launch {
                    db.replaceItem(event.item)
                    db.getItems().let {
                        _state.update { copy(items = it, selectedItem = null, image = null) }
                    }
                }
            }

            is AppEvent.ShowAddItem -> {
                _state.update { copy(showAddItem = event.set) }
            }

            is AppEvent.ShowEditItem -> {
                if (event.set) {
                    _state.update { copy(selectedItem = event.item, showEditItem = event.set) }
                } else {
                    _state.update {
                        copy(
                            selectedItem = event.item,
                            showEditItem = event.set,
                            image = null
                        )
                    }
                }
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
}


