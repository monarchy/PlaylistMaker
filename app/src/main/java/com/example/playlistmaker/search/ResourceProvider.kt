package com.example.playlistmaker.search

interface ResourcesProvider {
    fun getNothingFoundText(): String
    fun getSomethingWentWrongText(): String
}