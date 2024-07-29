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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ObjectCannedAcl
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import storify.MainViewModel
import core.model.Strings.localized
import kotlinx.coroutines.runBlocking
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.io.path.pathString


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


///
actual fun ImageBitmap.convert(): ByteArray {
    val bufferedImage = BufferedImage(
        width,
        height,
        BufferedImage.TYPE_INT_ARGB
    )

    // Copy the ImageBitmap to BufferedImage
    val bitmap = toAwtImage()
    bufferedImage.graphics.drawImage(bitmap, 0, 0, null)

    // Convert BufferedImage to ByteArray
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", outputStream)

    println(outputStream.toByteArray().toString())

    return outputStream.toByteArray()
}


actual fun ByteArray.byteArrayToImageBitmap(): ImageBitmap? {
    return try {
        // Convert ByteArray to BufferedImage
        val inputStream = ByteArrayInputStream(this)
        val bufferedImage: BufferedImage = ImageIO.read(inputStream)

        // Convert BufferedImage to ImageBitmap
        bufferedImage.toComposeImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


actual fun getFilePath(fileName: String): String {
    val userHome = System.getProperty("user.home")
    return Paths.get(userHome, fileName).pathString
}

actual suspend fun uploadImageToS3(image: ImageBitmap): String {
    val imageBytes: ByteArray = image.convert()
    val bucketName = "storifyimagedf020-dev"
    val contentType = "image/jpeg"

    val s3 = S3Client { region = "eu-west-3" }
    val keyName = "public/"+UUID.randomUUID().toString() + ".jpg"

    val request = PutObjectRequest {
        bucket = bucketName
        key = keyName
        body = ByteStream.fromBytes(imageBytes)
        acl = ObjectCannedAcl.PublicRead
        this.contentType = contentType
    }

    s3.putObject(request)


    val fileUrl = "https://$bucketName.s3.eu-west-3.amazonaws.com/$keyName"

    s3.close()
    println(fileUrl)
    return fileUrl
}