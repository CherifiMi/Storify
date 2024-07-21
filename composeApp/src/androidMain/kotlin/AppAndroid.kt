import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import storify.MainViewModel

@Composable
actual fun ImagePicker(viewModel: MainViewModel) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                viewModel.updateImage(bitmap.asImageBitmap())
            }
        }
    )

    Card(
        Modifier
            .fillMaxWidth()
            .clickable { launcher.launch("image/*") }
    ) {
        state.image?.let {
            Image(
                bitmap = it,
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize()
            )
        } ?: Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }
    }
}