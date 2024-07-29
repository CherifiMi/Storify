package data

import androidx.compose.ui.graphics.ImageBitmap
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ObjectCannedAcl
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import convert
import java.util.UUID

suspend fun uploadImageToS3(image: ImageBitmap): String {
    val imageBytes: ByteArray = image.convert()
    val bucketName = "storifyimages"
    val contentType = "image/jpeg"

    val s3 = S3Client { region = "eu-west-3" }
    val keyName = UUID.randomUUID().toString() + ".jpg" // Change the extension if needed

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