package storify.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.theme.liteGray
import core.util.sortItems
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import data.Strings.localized
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_box
import storify.composeapp.generated.resources.ic_edit
import storify.composeapp.generated.resources.ic_min
import storify.composeapp.generated.resources.ic_plus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemTable(viewModel: MainViewModel = koinInject()) {

    val state = viewModel.state.value

    Box {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    val titles = listOf(
                        "Name".localized,
                        "Quantity".localized,
                        "Whole price".localized,
                        "Selling price".localized,
                        "Profit".localized,
                        "Expiration date".localized,
                    )
                    titles.forEach { title ->
                        Row(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.onEvent(AppEvent.SetFilter(title)) }
                                .weight(1f)
                        ) {
                            state.filer.let {
                                val firstChar = it.substring(0, 1)
                                val restOfString = it.substring(1)

                                if (restOfString == title) {
                                    val arow = when (firstChar) {
                                        "+" -> "▲"
                                        "-" -> "▼"
                                        else -> " "
                                    }
                                    Text(
                                        text = arow,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = liteGray
                                    )
                                }
                            }
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = liteGray
                            )
                        }
                    }
                    Row(modifier = Modifier.weight(.5f)) {}
                }
            }


            items(state.items.sortItems(state.filer)) { item ->
                if (item.name.contains(state.searchText)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                        Row(modifier = Modifier.weight(1f)) {

                            item.image?.let {//.byteArrayToImageBitmap()?
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp).padding(end = 8.dp),
                                )
                            } ?: Image(
                                modifier = Modifier.size(30.dp).padding(end = 8.dp),
                                painter = painterResource(Res.drawable.ic_box),
                                contentDescription = null
                            )

                            Text(
                                fontSize = 14.sp,
                                text = item.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colors.onBackground
                            )

                        }
                        Text(
                            fontSize = 14.sp,
                            text = item.quantity.toString(),
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            fontSize = 14.sp,
                            text = if (state.calc == "whole") item.wholePrice.times(item.quantity)
                                .toString() else item.wholePrice.toString(),
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            fontSize = 14.sp,
                            text = if (state.calc == "whole") item.sellingPrice.times(item.quantity)
                                .toString() else item.sellingPrice.toString(),
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            fontSize = 14.sp,
                            text = if (state.calc == "whole") item.profit.times(item.quantity)
                                .toString() else item.profit.toString(),
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colors.onBackground
                        )
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(12.dp)
                                    .background(Color.Transparent, RoundedCornerShape(4.dp)).border(
                                        BorderStroke(3.dp, item.expirationDate.getExpColor()),
                                        RoundedCornerShape(4.dp)
                                    )
                            )// exp color
                            Spacer(Modifier.size(8.dp))
                            Text(
                                fontSize = 14.sp,
                                text = item.expirationDate,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colors.onBackground
                            )
                        }
                        Row(
                            modifier = Modifier.weight(.5f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            /*Card(
                                modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
                                onClick = { viewModel.onEvent(AppEvent.FlipGrid) },
                                backgroundColor = MaterialTheme.colors.secondary
                            ) {
                                Icon(
                                    tint = MaterialTheme.colors.primary,
                                    painter = painterResource(Res.drawable.ic_min),
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Card(
                                modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
                                onClick = { viewModel.onEvent(AppEvent.FlipGrid) },
                                backgroundColor = MaterialTheme.colors.secondary
                            ) {
                                Icon(
                                    tint = MaterialTheme.colors.primary,
                                    painter = painterResource(Res.drawable.ic_plus),
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }*/
                            Spacer(Modifier.width(16.dp))
                            Card(
                                modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
                                onClick = { viewModel.onEvent(AppEvent.EditItem(item)) },
                                backgroundColor = MaterialTheme.colors.secondary
                            ) {
                                Icon(
                                    tint = MaterialTheme.colors.primary,
                                    painter = painterResource(Res.drawable.ic_edit),
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(100.dp))
            }
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddButton()
        }

    }
}

fun String.getExpColor(): Color {
    val exp_level = getExpirationLevel(this)

    return when(exp_level){
        0 -> Color.Gray.copy(alpha = .7f)
        1 -> Color.Green.copy(alpha = .7f)
        2 -> Color.Yellow.copy(alpha = .7f)
        3 -> Color.Red.copy(alpha = .7f)
        else -> Color.Gray.copy(alpha = .7f)
    }
}
fun getExpirationLevel(dateString: String): Int {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("d/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    )

    val expirationDate: LocalDate? = formatters.asSequence().mapNotNull { formatter ->
        try {
            LocalDate.parse(dateString, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }.firstOrNull()

    return if (expirationDate != null) {
        val currentDate = LocalDate.now()
        when {
            expirationDate.isBefore(currentDate) -> 3
            expirationDate.isBefore(currentDate.plusMonths(1)) -> 2
            expirationDate.isAfter(currentDate.plusMonths(1)) -> 1
            else -> 0
        }
    } else {
        0
    }
}
