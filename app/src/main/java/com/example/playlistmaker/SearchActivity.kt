package com.example.playlistmaker

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
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
    private var currentSearchText: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        searchHistoryAdapter = SearchHistoryAdapter(this)

        //RV
        recyclerView = findViewById(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(filteredTracks) { track ->
            if (clickDebounce()) {
                searchHistoryAdapter.addTrack(track)
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
                    putExtra(Constants.PREVIEW_URL, track.previewUrl)
                }
                startActivity(intentAudioPlayerActivity)
        }
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
                currentSearchText = searchText

                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchDebounce()

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
        RetrofitInstance.apiService.searchSongs(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
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
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true
            else -> false
        }
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
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        if (searchText.isEmpty()){
            return
        }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private val searchRunnable = Runnable { searchSongs(query = currentSearchText) }
     }







