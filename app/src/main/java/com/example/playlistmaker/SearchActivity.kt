package com.example.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import retrofit2.Call

class SearchActivity : AppCompatActivity() {

    private var searchText by mutableStateOf("")
    private var filteredTracks = mutableStateListOf<Track>()
    private var isLoading by mutableStateOf(false)
    private var isError by mutableStateOf(false)
    private var isEmpty by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем Compose как основной UI
        setContent {
            SearchScreen(
                searchText = searchText,
                onSearchTextChanged = { newText -> searchText = newText },
                onSearch = { query -> searchSongs(query) },
                isLoading = isLoading,
                isError = isError,
                isEmpty = isEmpty,
                filteredTracks = filteredTracks,
                onRetry = { searchSongs(searchText) }
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    private fun searchSongs(query: String) {
        if (!isConnectedToInternet()) {
            isError = true
            return
        }
        isLoading = true
        RetrofitInstance.apiService.searchSongs(query).enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: retrofit2.Response<SearchResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val songs = response.body()?.results ?: emptyList()
                    if (songs.isEmpty()) {
                        isEmpty = true
                    } else {
                        filteredTracks.clear()
                        filteredTracks.addAll(songs)
                    }
                } else {
                    isError = true
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                isLoading = false
                isError = true
            }
        })
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }
}



