package storify.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.components.MongoImage
import core.theme.liteGray
import core.util.sortItems
import domain.model.Item
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import core.model.Strings.localized
import core.util.getExpColor
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemGrid(viewModel: MainViewModel = koinInject(), columns: Int = 5) {
    val state = viewModel.state.value

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns)
    ) {
        items(state.items.sortItems(state.filer)) { item ->
            if (item.name.contains(state.searchText)) {
                GridItemView(item)
            }
        }

        item {
            Box(modifier = Modifier.padding(16.dp)) {
                Card(
                    onClick = { viewModel.onEvent(AppEvent.ShowAddItem(true)) },
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.secondary,
                    elevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("+ add item".localized, fontSize = 12.sp, color = MaterialTheme.colors.primary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GridItemView(item: Item, viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    val titles = listOf(
        "Name".localized,
        "Quantity".localized,
        "Whole price".localized,
        "Selling price".localized,
        "Profit".localized,
    )

    Card(modifier = Modifier.padding(8.dp), backgroundColor = MaterialTheme.colors.background) {
        Column(Modifier.padding(bottom = 16.dp)) {

            Card(Modifier.fillMaxWidth().aspectRatio(2f)) {
                MongoImage(modifier = Modifier.size(30.dp).padding(end = 8.dp), image_url = item.image_id)
            }

            titles.forEachIndexed { index, title ->
                val value = when (index) {
                    0 -> item.name
                    1 -> item.quantity.toString()
                    2 -> if (state.calc == "whole") item.wholePrice.times(item.quantity).toString() else item.wholePrice.toString()
                    3 -> if (state.calc == "whole") item.sellingPrice.times(item.quantity).toString() else item.sellingPrice.toString()
                    4 -> if (state.calc == "whole") item.profit.times(item.quantity).toString() else item.profit.toString()
                    else -> ""
                }
                LabelCard(title, value)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(8.dp))
                Box(
                    Modifier.size(12.dp)
                        .background(Color.Transparent, RoundedCornerShape(4.dp))
                        .border(BorderStroke(3.dp, item.expirationDate.getExpColor()), RoundedCornerShape(4.dp))
                )
                LabelCard("Expiration date".localized.split(" ")[0], item.expirationDate)
            }

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair(Res.drawable.ic_min, { viewModel.onEvent(AppEvent.MinItem(item)) }),
                    Pair(Res.drawable.ic_plus, { viewModel.onEvent(AppEvent.PlusItem(item)) }),
                    Pair(Res.drawable.ic_edit, { viewModel.onEvent(AppEvent.ShowEditItem(true, item)) })
                ).forEach { (icon, action) ->
                    Card(
                        modifier = Modifier.padding(vertical = 8.dp).size(32.dp),
                        onClick = action,
                        backgroundColor = MaterialTheme.colors.secondary
                    ) {
                        Icon(
                            tint = MaterialTheme.colors.primary,
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
fun LabelCard(title: String, value: String) {
    Row {
        Text(
            text = "$title: ",
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = liteGray,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
