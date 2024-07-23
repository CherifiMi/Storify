package storify.components

import ImagePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import core.model.Item
import data.Strings.localized
import org.koin.compose.koinInject
import storify.MainViewModel

@Composable
fun AddItemDialog(
    viewModel: MainViewModel = koinInject(),
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit
) {
    val state = viewModel.state.value

    LaunchedEffect(Unit){

    }

    var id by remember { mutableStateOf(state.selectedItem?._id ?: "") }
    var name by remember { mutableStateOf(state.selectedItem?.name ?:"") }
    var quantity by remember { mutableStateOf(state.selectedItem?.quantity?.toString() ?:"") }
    var wholePrice by remember { mutableStateOf(state.selectedItem?.wholePrice?.toString() ?:"") }
    var sellingPrice by remember { mutableStateOf(state.selectedItem?.sellingPrice?.toString() ?:"") }

    var expirationDateDay by remember { mutableStateOf(state.selectedItem?.expirationDate?.split("/")?.getOrNull(0) ?:"") }
    var expirationDateMonth by remember { mutableStateOf(state.selectedItem?.expirationDate?.split("/")?.getOrNull(1)  ?:"") }
    var expirationDateYear by remember { mutableStateOf(state.selectedItem?.expirationDate?.split("/")?.getOrNull(2)  ?:"") }

    Dialog(onDismissRequest = onDismiss) {
        Card(elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {

                ImagePicker(viewModel)

                Text("Add New Item".localized, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name".localized) })
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity".localized) })
                OutlinedTextField(
                    value = wholePrice,
                    onValueChange = { wholePrice = it },
                    label = { Text("Whole price".localized) })
                OutlinedTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = { Text("Selling price".localized) })
                Row(Modifier.width(280.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = expirationDateDay,
                        onValueChange = { expirationDateDay = it },
                        label = { Text("Day".localized) })
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = expirationDateMonth,
                        onValueChange = { expirationDateMonth = it },
                        label = { Text("Month".localized) })
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = expirationDateYear,
                        onValueChange = { expirationDateYear = it },
                        label = { Text("Year".localized) })
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = onDismiss) {
                        Text("Cancel".localized)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {

                        val item = Item(
                            _id = id,
                            name = name,
                            image = state.image/*.convert()*/,
                            quantity = quantity.toIntOrNull() ?: 0,
                            wholePrice = wholePrice.toDoubleOrNull() ?: 0.0,
                            sellingPrice = sellingPrice.toDoubleOrNull() ?: 0.0,
                            profit = (sellingPrice.toDoubleOrNull() ?: 0.0) - (wholePrice.toDoubleOrNull() ?: 0.0),
                            expirationDate = "$expirationDateDay/$expirationDateMonth/$expirationDateYear"
                        )
                        onSave(item)
                        onDismiss()
                    }) {
                        Text("Save".localized)
                    }
                }
            }
        }
    }
}