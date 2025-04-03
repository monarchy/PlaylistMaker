package com.example.playlistmaker.search.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ResourcesProvider

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getNothingFoundText(): String {
        return context.getString(R.string.placeholder_no_results)
    }

    override fun getSomethingWentWrongText(): String {
        return context.getString(R.string.placeholder_error)
    }
}