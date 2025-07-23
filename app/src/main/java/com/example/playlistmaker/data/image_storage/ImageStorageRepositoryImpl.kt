package com.example.playlistmaker.data.image_storage

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.example.playlistmaker.domain.image_storage.ImageStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageStorageRepositoryImpl(
    private val picturesDir: File, private val contentResolver: ContentResolver
) : ImageStorageRepository {
    override suspend fun uploadImage(imagePath: String): String = withContext(Dispatchers.IO) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(picturesDir, fileName)

        contentResolver.openInputStream(imagePath.toUri()).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            }
        }
        "file://${file}"
    }

    override suspend fun deleteFromStorage(fileUri: String) {
        withContext(Dispatchers.IO) {
            val cleanPath = fileUri.removePrefix("file://")
            File(cleanPath).delete()
        }
    }

    override suspend fun isContainsInStorage(fileUri: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val fileName = File(fileUri.removePrefix("file://")).name
                val file = File(picturesDir, fileName)

                file.exists() && file.isFile
            } catch (e: Exception) {
                false
            }
        }
}

