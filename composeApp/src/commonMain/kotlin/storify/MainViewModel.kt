package storify

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable
import androidx.compose.runtime.State
import core.model.Item
import core.util.update
import domain.MongoDBService
import kotlinx.coroutines.launch
import storify.composeapp.generated.resources.Res
import java.util.ResourceBundle



object Strings {
    private val en = mapOf(
        "All items" to "All items",
        "Add New Item" to "Add New Item",
        "Name" to "Name",
        "Quantity" to "Quantity",
        "Whole price" to "Whole Price",
        "Selling price" to "Selling Price",
        "Expiration date" to "Expiration Date",
        "Cancel" to "Cancel",
        "Save" to "Save",
        "Profit" to "Profit",
        "total whole price" to "Total Whole Price",
        "total selling price" to "Total Selling Price",
        "total profit" to "Total Profit",
        "Storify" to "Storify",
        "Search.." to "Search..",
        "+ add item" to "+ add item"
    )

    private val ar = mapOf(
        "All items" to "جميع العناصر",
        "Add New Item" to "إضافة عنصر جديد",
        "Name" to "الاسم",
        "Quantity" to "الكمية",
        "Whole price" to "السعر الكامل",
        "Selling price" to "سعر البيع",
        "Expiration date" to "تاريخ انتهاء الصلاحية",
        "Cancel" to "إلغاء",
        "Save" to "حفظ",
        "Profit" to "الربح",
        "total whole price" to "إجمالي السعر الكامل",
        "total selling price" to "إجمالي سعر البيع",
        "total profit" to "إجمالي الربح",
        "Storify" to "ستوريفاي",
        "Search.." to "بحث..",
        "+ add item" to "+ إضافة عنصر"
    )


    private var currentLanguage = en

    fun setLanguage(language: String) {
        currentLanguage = when (language) {
            "ar" -> ar
            else -> en
        }
    }


    val String.localized: String
        get() = getString(this)


    fun getString(key: String): String {
        return currentLanguage[key] ?: key
    }
}

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

    val englishStrings = mapOf(
        "hello" to "Hello",
        "welcome" to "Welcome",
        "All items" to "All items"
    )

    val arabicStrings = mapOf(
        "hello" to "مرحبا",
        "welcome" to "أهلا وسهلا",
        "All items" to "أهلا وسهلا"
    )

    val strings = when (state.value.lang) {
        "en" -> englishStrings
        "ar" -> arabicStrings
        else -> englishStrings
    }

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
}

fun String.flip(): String {
    return if (this == "+") "-" else "+"
}

