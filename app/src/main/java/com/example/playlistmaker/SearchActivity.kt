package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.Response
import retrofit2.Call

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var goBackButton: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var placeholderText: TextView
    private lateinit var errorPlaceHolder: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var emptyPlaceholder: LinearLayout
    private var searchText: String = ""
    private val filteredTracks = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()

        //RV
        recyclerView = findViewById(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(filteredTracks)
        recyclerView.adapter = adapter
        //Плейсхолдер с ошибкой и кнопкой
        errorPlaceHolder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
        //Основная вью с поиском
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        goBackButton = findViewById(R.id.goBackButton)
        placeholderText = findViewById(R.id.placeholderTextView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)

        //Меняем цвет курсора
        searchEditText.textCursorDrawable = ContextCompat.getDrawable(this, R.drawable.custom_cursor)

        //Кнопка назад
        goBackButton.setNavigationOnClickListener { finish() }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (searchText.isNotEmpty()) {
                    searchSongs(searchText)
                } else {
                    showEmptyPlaceholder()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // Очистка текста
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        // Кнопка "Обновить"
        retryButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchSongs(searchText)
            }
        }

        // Восстановление текста при повороте экрана
        savedInstanceState?.let {
            searchText = it.getString("SEARCH_TEXT", "") ?: ""
            searchEditText.setText(searchText)
            searchSongs(searchText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    private fun searchSongs(query: String) {
        if (!isConnectedToInternet()) {
            showErrorPlaceholder()  // Новый плейсхолдер для отсутствия интернета
            return
        }
        RetrofitInstance.apiService.searchSongs(query).enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: retrofit2.Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val songs = body?.results ?: emptyList()
                    if (songs.isEmpty()) {
                        showEmptyPlaceholder()
                    } else {
                        showResults(songs)
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showErrorPlaceholder()
            }
        })
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork : NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun showResults(songs: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        errorPlaceHolder.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE

        filteredTracks.clear()
        filteredTracks.addAll(songs)
        adapter.notifyDataSetChanged()
    }

    private fun showErrorPlaceholder() {
        recyclerView.visibility = View.GONE
        errorPlaceHolder.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
    }

    private fun showEmptyPlaceholder() {
        recyclerView.visibility = View.GONE
        errorPlaceHolder.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
    }

}



