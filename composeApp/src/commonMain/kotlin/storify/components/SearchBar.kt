package storify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import storify.AppEvent
import storify.MainViewModel
import core.model.Strings.localized
import storify.composeapp.generated.resources.Res
import storify.composeapp.generated.resources.ic_search

@Composable
fun SearchBar(viewModel: MainViewModel = koinInject(), show: Boolean = true) {

    val state = viewModel.state.value

    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 16.dp).fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (show){
            Text(
                "Storify".localized,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(modifier = Modifier.weight(2f))
        }


        TextField(
            value = state.searchText,
            onValueChange = { newValue -> viewModel.onEvent(AppEvent.UpdateSearchText(newValue)) },
            leadingIcon = {
                Icon(
                    modifier = Modifier.padding(horizontal = 16.dp).size(24.dp),
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null
                )
            },
            placeholder = {
                Text(
                    text = "Search..".localized,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier.weight(1f).height(56.dp).shadow(1.dp)
                .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(4.dp)),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.surface,
                textColor = MaterialTheme.colors.onBackground
            )
        )
    }
}