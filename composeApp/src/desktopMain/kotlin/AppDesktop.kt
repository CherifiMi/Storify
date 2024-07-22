import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import storify.MainViewModel
import data.Strings.localized
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import javax.imageio.ImageIO


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
actual fun ImagePicker(viewModel: MainViewModel) {
    val state = viewModel.state.value

    Card(
        modifier = Modifier.width(280.dp).aspectRatio(10f / 3f), onClick = {
            val fileDialog = FileDialog(Frame(), "Choose an image", FileDialog.LOAD)
            fileDialog.isVisible = true
            val selectedFile = fileDialog.files.firstOrNull()

            if (selectedFile != null) {
                val image = ImageIO.read(File(selectedFile.absolutePath))
                viewModel.updateImage(image.toComposeImageBitmap())
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    ) {
        state.image?.let {
            Image(
                bitmap = it,
                contentDescription = "Selected Image",
                modifier = Modifier
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select Image".localized)
        }
    }
}

