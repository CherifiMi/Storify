import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.amplifyframework.core.Amplify
import core.model.Strings.localized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import storify.MainViewModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


lateinit var filedir: String

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
actual fun ImagePicker(viewModel: MainViewModel) {
    val state = viewModel.state.value

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap =
                    BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                viewModel.updateImage(bitmap.asImageBitmap())
            }
        }
    )

    Card(
        modifier = Modifier.width(280.dp).aspectRatio(10f / 3f), onClick = {
            launcher.launch("image/*")
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

actual fun ImageBitmap.convert(): ByteArray {
    val bitmap = this.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

    return outputStream.toByteArray()
}

actual fun ByteArray.byteArrayToImageBitmap(): ImageBitmap? {
    return try {
        val bitmap: Bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(this))
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

actual fun getFilePath( fileName: String): String {
    return "${filedir}/$fileName"
}



/////////////////

fun ByteArray.toFile(): File {
    val file = File.createTempFile("image", ".jpg")
    FileOutputStream(file).use { out ->
        out.write(this)
    }
    return file
}

actual suspend fun uploadImageToS3(image: ImageBitmap): String {
    return withContext(Dispatchers.IO) {
        val file = image.convert().toFile()
        val keyName = UUID.randomUUID().toString() + ".jpg"

        Amplify.Storage.uploadFile(
            keyName,
            file,
            {result -> result.key },
            {error -> Log.e("MITOTEST", "Failed upload", error) }
        )


        val bucketName = "storifyimagedf020-dev"
        val fileUrl = "https://$bucketName.s3.eu-west-3.amazonaws.com/public/$keyName"

        Log.d("MITOTEST", "File URL: $fileUrl")
        fileUrl
    }
}
