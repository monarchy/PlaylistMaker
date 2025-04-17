package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchState
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.util.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel by viewModel<SearchViewModel>()


    private lateinit var binding: ActivitySearchBinding
    private var userText = ""

    private val trackAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackClicked(it)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchRecyclerView.adapter = trackAdapter

        setupListeners()
        viewModel.searchState.observe(this) {
            render(it)
        }

        viewModel.toastState.observe(this) {
            it?.let { showToast(it) }
        }
        viewModel.navigateToPlayer.observe(this) { track ->
            val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra(Constants.TRACK, track)
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
            startActivity(intent)
        }
        viewModel.onCreate()

    }

    private fun setupListeners() {

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        binding.goBackButton.setOnClickListener {
            finish()
        }

        binding.clearButton.setOnClickListener {
            viewModel.onClearButtonClicked()
            binding.searchEditText.setText("")
            binding.clearButton.visibility = View.GONE
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }

        binding.cleanHistoryButton.setOnClickListener {
            viewModel.onCleanHistoryClicked()
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
                if (binding.searchEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.showSearchHistory()
                } else {
                    viewModel.hideSearchHistory()
                }

                if (s.isNullOrEmpty()) {
                    binding.placeholderImage.visibility = View.GONE
                    binding.placeholderMessage.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userText = s.toString()
            }
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        binding.updateButton.setOnClickListener {
            viewModel.onUpdateButtonClicked()
        }
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.history)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    private fun updateUI(
        showProgressBar: Boolean = false,
        showPlaceholderImage: Boolean = false,
        placeholderImageRes: Int? = null,
        showUpdateButton: Boolean = false,
        showCleanHistoryButton: Boolean = false,
        showPlaceholderMessage: Boolean = false,
        message: String? = null
    ) {
        binding.progressBar.isVisible = showProgressBar
        binding.placeholderImage.isVisible = showPlaceholderImage
        placeholderImageRes?.let { binding.placeholderImage.setImageResource(it) }
        binding.updateButton.isVisible = showUpdateButton
        binding.cleanHistoryButton.isVisible = showCleanHistoryButton
        binding.placeholderMessage.isVisible = showPlaceholderMessage
        message?.let { binding.placeholderMessage.text = it }
    }

    private fun showLoading() {
        updateUI(showProgressBar = true)
    }

    private fun showError(errorMessage: String) {
        updateUI(
            showPlaceholderImage = true,
            placeholderImageRes = R.drawable.placeholder_error,
            showUpdateButton = true,
            showPlaceholderMessage = true,
            message = errorMessage
        )
    }

    private fun showEmpty(emptyMessage: String) {
        updateUI(
            showPlaceholderImage = true,
            placeholderImageRes = R.drawable.placeholder_nothing_found,
            showPlaceholderMessage = true,
            message = emptyMessage
        )
    }

    private fun showContent(tracks: List<Track>) {
        updateUI()
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistory(history: List<Track>) {
        updateUI(showCleanHistoryButton = true)
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(history)
        trackAdapter.notifyDataSetChanged()
    }
}