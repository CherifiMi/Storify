package storify.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.model.Item
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemGrid(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 260.dp)
    ) {
        items(state.items.sortItems(state.filer)) { item ->
            GridItemView(item)
        }

        item{
            Box(modifier = Modifier.size(100.dp).fillMaxSize().padding(16.dp)){
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

    val titles = listOf(
        "Name".localized,
        "Quantity".localized,
        "Whole price".localized,
        "Selling price".localized,
        "Profit".localized,
        "Expiration date".localized
    )

    Card(modifier = Modifier.padding(8.dp), backgroundColor = MaterialTheme.colors.background){
        Column(Modifier.padding(vertical = 16.dp)){

            Card(Modifier.fillMaxWidth().aspectRatio(2f)) {
                item.image?.byteArrayToImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 8.dp),
                        contentScale = ContentScale.Crop,  // Add this line to crop the image
                    )
                } ?: Image(
                    modifier = Modifier.size(30.dp).padding(end = 8.dp),
                    painter = painterResource(Res.drawable.ic_box),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }


            LableCard(titles[0],item.name)
            LableCard(titles[1],item.quantity.toString())
            LableCard(titles[2],item.wholePrice.toString())
            LableCard(titles[3],item.sellingPrice.toString())
            LableCard(titles[4],item.profit.toString())
            LableCard(titles[5],item.expirationDate)

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(32.dp),
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
                    modifier = Modifier.size(32.dp),
                    onClick = { viewModel.onEvent(AppEvent.FlipGrid) },
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
                    modifier = Modifier.size(32.dp),
                    onClick = { viewModel.onEvent(AppEvent.FlipGrid) },
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
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = liteGray,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = name,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(8.dp)
        )
    }
}
