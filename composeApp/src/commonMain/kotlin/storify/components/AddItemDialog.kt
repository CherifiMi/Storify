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
import domain.model.Item
import core.model.Strings.localized
import org.koin.compose.koinInject
import storify.MainViewModel

@Composable
fun AddItemDialog(
    viewModel: MainViewModel = koinInject(),
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit
) {
    val state = viewModel.state.value

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var wholePrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var expirationDateDay by remember { mutableStateOf("") }
    var expirationDateMonth by remember { mutableStateOf("") }
    var expirationDateYear by remember { mutableStateOf("") }

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
                            name = name,
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