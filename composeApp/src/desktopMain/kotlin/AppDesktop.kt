import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import storify.MainViewModel
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO


@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun ImagePicker(viewModel: MainViewModel) {
    val state = viewModel.state.value
    Card(Modifier.fillMaxWidth().onClick {
        val fileDialog = FileDialog(Frame(), "Choose an image", FileDialog.LOAD)
        fileDialog.isVisible = true
        val selectedFile = fileDialog.files.firstOrNull()

        if (selectedFile != null) {
            val image = ImageIO.read(File(selectedFile.absolutePath))
            viewModel.updateImage(image.toComposeImageBitmap())
        }
    }) {
        state.image?.let {
            Image(
                bitmap = it ,
                contentDescription = "Selected Image",
                modifier = Modifier
            )
        } ?: Button(onClick = {}){}
    }
}