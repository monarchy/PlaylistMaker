package com.example.playlistmaker.util

sealed class Resource<T>(val data: T? = null, val errorState: SearchState? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorState: SearchState) : Resource<T>(errorState = errorState)
}