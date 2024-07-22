package storify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.MainViewModel
import data.Strings.localized
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_cart2
import storify.composeapp.generated.resources.ic_profit
import storify.composeapp.generated.resources.ic_truck

@Composable
fun TotalCards(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    Row(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp).padding(vertical = 16.dp)
                .weight(1f).height(80.dp)
                .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(4.dp))
                .shadow(1.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "total whole price".localized,
                    color = MaterialTheme.colors.surface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    "1000,00DZD",
                    color = MaterialTheme.colors.surface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                modifier = Modifier.size(45.dp),
                tint = MaterialTheme.colors.secondary,
                painter = painterResource(Res.drawable.ic_truck),
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier.padding(start = 16.dp).padding(vertical = 16.dp)
                .weight(1f).height(80.dp)
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "total selling price".localized,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    "1000,00DZD",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                modifier = Modifier.size(45.dp),
                tint = MaterialTheme.colors.primary,
                painter = painterResource(Res.drawable.ic_cart2),
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier.padding(start = 16.dp).padding(vertical = 16.dp)
                .weight(1f).height(80.dp)
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "total profit         ".localized,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    "1000,00DZD",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                modifier = Modifier.size(45.dp),
                tint = MaterialTheme.colors.primary,
                painter = painterResource(Res.drawable.ic_profit),
                contentDescription = null
            )
        }


        Spacer(Modifier.weight(1f))
    }
}