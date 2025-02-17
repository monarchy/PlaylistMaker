package com.example.playlistmaker

import android.content.Context
import android.content.Intent
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var youSearched: TextView
    private lateinit var cleanHistoryButton: Button
    private var searchText: String = ""
    private val filteredTracks = ArrayList<Track>()
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        searchHistoryAdapter = SearchHistoryAdapter(this)

        //RV
        recyclerView = findViewById(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(filteredTracks) { track ->
            searchHistoryAdapter.addTrack(track)
                //Log.d("click", "$track.trackTime")
            val intentAudioPlayerActivity = Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra(Constants.TRACK_ID, track.trackId)
                putExtra(Constants.TRACK_NAME, track.trackName)
                putExtra(Constants.ARTIST_NAME, track.artistName)
                putExtra(Constants.COLLECTION_NAME, track.collectionName)
                putExtra(Constants.RELEASE_DATE, track.releaseDate)
                putExtra(Constants.PRIMARY_GENRE_NAME, track.primaryGenreName)
                putExtra(Constants.COUNTRY, track.country)
                putExtra(Constants.TRACK_TIME_MILLIS, track.trackTimeMillis)
                putExtra(Constants.ART_WORK_URL, track.artworkUrl100)
            }
            startActivity(intentAudioPlayerActivity)
        }
        recyclerView.adapter = adapter
        //Плейсхолдер с ошибкой и кнопкой
        errorPlaceHolder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
        //Основная вью с поиском
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        goBackButton = findViewById(R.id.goBackButton)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        progressBar = findViewById(R.id.progressBar)
        youSearched = findViewById(R.id.you_searched_text)
        cleanHistoryButton = findViewById(R.id.cleanHistoryButton)

        searchEditText.textCursorDrawable = ContextCompat.getDrawable(this, R.drawable.custom_cursor)


        goBackButton.setNavigationOnClickListener { finish() }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && searchHistoryAdapter.getHistory().isNotEmpty()) {
                showHistory()
            } else {
                hideSearchHistory()
            }
        }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()

                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (searchText.isEmpty() && searchEditText.hasFocus()) {
                    showHistory()
                    cleanHistoryButton.visibility = if (searchHistoryAdapter.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
                    youSearched.visibility = if (searchHistoryAdapter.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    hideSearchHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchText.isNotEmpty()) {
                    searchSongs(searchText)
                    cleanHistoryButton.visibility = View.GONE
                    youSearched.visibility = View.GONE
                }
                true
            } else {
                false
            }
        }

        cleanHistoryButton.setOnClickListener {
            searchEditText.text.clear()
            youSearched.visibility = View.GONE
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchHistoryAdapter.clearHistory()
            filteredTracks.clear()
            adapter.notifyDataSetChanged()
        }
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }


        retryButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchSongs(searchText)
            }
        }

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
            showErrorPlaceholder()
            return
        }
        showLoader()
        RetrofitInstance.apiService.searchSongs(query).enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: retrofit2.Response<SearchResponse>
            ) {
                hideLoader()
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
                hideLoader()
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

    private fun showLoader() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceHolder.visibility = View.GONE
    }

    private fun hideLoader() {
        progressBar.visibility = View.GONE
    }
    private fun hideResultsAndPlaceholders() {
        recyclerView.visibility = View.GONE
        errorPlaceHolder.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        progressBar.visibility = View.GONE
        filteredTracks.clear()
        adapter.notifyDataSetChanged()
    }
    private fun showHistory() {
        val history = searchHistoryAdapter.getHistory()
        if (history.isEmpty()) {
            showEmptyPlaceholder()
        } else {
            recyclerView.visibility = View.VISIBLE
            errorPlaceHolder.visibility = View.GONE
            emptyPlaceholder.visibility = View.GONE
            progressBar.visibility = View.GONE
            youSearched.visibility = View.VISIBLE
            cleanHistoryButton.visibility = View.VISIBLE
            adapter.bindHistory(history)
        }
    }
    private fun hideSearchHistory() {
        recyclerView.visibility = View.GONE
        cleanHistoryButton.visibility = View.GONE
        youSearched.visibility = View.GONE
    }
}






