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
        fileName
    }

    override suspend fun getImage(fileName: String?): String? {
        if (fileName.isNullOrEmpty()) return null
        return File(picturesDir, fileName).absolutePath
    }

    override suspend fun deleteFromStorage(fileName: String) {
        withContext(Dispatchers.IO) {
            File(picturesDir, fileName).delete()
        }

    }
}

