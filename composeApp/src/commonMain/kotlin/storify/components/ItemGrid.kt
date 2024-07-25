package storify.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.Item
import core.theme.liteGray
import core.util.sortItems
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import core.model.Strings.localized
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_box
import storify.composeapp.generated.resources.ic_edit
import storify.composeapp.generated.resources.ic_min
import storify.composeapp.generated.resources.ic_plus


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemGrid(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp)
    ) {
        items(state.items.sortItems(state.filer)) { item ->
            if (item.name.contains(state.searchText)) {
                GridItemView(item)
            }
        }

        item{
            Box(modifier = Modifier.size(160.dp).fillMaxSize().padding(16.dp)){
                Card(
                    onClick = { viewModel.onEvent(AppEvent.ShowAddItem(true)) },
                    modifier = Modifier.fillMaxSize(), // Ensure it fills the parent
                    contentColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.secondary,
                    elevation = 4.dp // Optional: Adjust elevation if needed
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
        "Expiration date".localized.split(" ")[0]
    )

    Card(modifier = Modifier.padding(8.dp), backgroundColor = MaterialTheme.colors.background){
        Column(Modifier.padding(bottom = 16.dp)){

            Card(Modifier.fillMaxWidth().aspectRatio(2f)) {
                var image by remember { mutableStateOf<ImageBitmap?>(null) }

                viewModel.drawImage(item.image_id ?: "") {
                    image = it
                }

                image?.let { it1 ->
                    Image(
                        bitmap = it1,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 8.dp),
                    )
                } ?: Image(
                    modifier = Modifier.size(30.dp).padding(end = 8.dp),
                    painter = painterResource(Res.drawable.ic_box),
                    contentDescription = null
                )

            }



            LableCard(titles[0],item.name)
            LableCard(titles[1],item.quantity.toString())
            LableCard(titles[2],if (state.calc == "whole") item.wholePrice.times(item.quantity).toString() else item.wholePrice.toString())
            LableCard(titles[3],if (state.calc == "whole") item.sellingPrice.times(item.quantity).toString() else item.sellingPrice.toString())
            LableCard(titles[4],if (state.calc == "whole") item.profit.times(item.quantity).toString() else item.profit.toString())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.size(8.dp))
                Box(
                    Modifier.size(12.dp)
                        .background(Color.Transparent, RoundedCornerShape(4.dp)).border(
                            BorderStroke(3.dp, item.expirationDate.getExpColor()),
                            RoundedCornerShape(4.dp)
                        )
                )
                LableCard(titles[5],item.expirationDate)
            }


            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.padding(vertical = 8.dp).size(32.dp),
                    onClick = { viewModel.onEvent(AppEvent.MinItem(item)) },
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
                    modifier = Modifier.padding(vertical = 8.dp).size(32.dp),
                    onClick = { viewModel.onEvent(AppEvent.PlusItem(item)) },
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(
                        tint = MaterialTheme.colors.primary,
                        painter = painterResource(Res.drawable.ic_plus),
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp)
                    )
                }



                Spacer(Modifier.width(16.dp))

                Card(
                    modifier = Modifier.padding(vertical = 8.dp).size(32.dp),
                    onClick = { viewModel.onEvent(AppEvent.ShowEditItem(true, item)) },
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

@Composable
fun LableCard(s: String, name: String) {
    Row {
        Text(
            text = s+": ",
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = liteGray,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = name,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
