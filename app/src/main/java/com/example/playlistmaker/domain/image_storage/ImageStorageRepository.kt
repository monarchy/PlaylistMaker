package com.example.playlistmaker.domain.image_storage

interface ImageStorageRepository {
    suspend fun uploadImage(imagePath: String): String
    suspend fun deleteFromStorage(fileName: String)
    suspend fun isContainsInStorage(fileUri:String):Boolean
}