package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.adapters.trackList.TrackListAdapter
import com.example.playlistmaker.ui.player.PlayerFragment
import com.example.playlistmaker.util.GsonClient
import com.example.playlistmaker.util.SearchState
import com.example.playlistmaker.util.click_listenners.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment() {
    private var searchQuery: String? = null
    private var isFirstLaunch = true
    private var isNotFirstResume = false
    private lateinit var searchAdapter: TrackListAdapter
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel { parametersOf(requireContext()) }
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var isTextProgrammaticChange = false

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (isNotFirstResume) {
            viewModel.resumeFragment()
        }
        isNotFirstResume = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        searchQuery = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.onTrackClicked(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(GsonClient.trackToJson(track))
            )
        }

        searchAdapter = TrackListAdapter(onTrackClickDebounce)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect() { state ->
                    when (state) {
                        SearchState.EmptyScreen -> emptyUi()
                        SearchState.Loading -> loadingUi()
                        SearchState.NetworkError -> networkErrorUi()
                        SearchState.NothingFound -> tracksNotFoundUi()
                        is SearchState.ShowHistoryContent -> historyUi(state.tracks)
                        is SearchState.ShowSearchContent -> contentUi(state.tracks)
                    }
                }
            }
        }

        if (isFirstLaunch) {
            binding.searchHint.requestFocus()
            isFirstLaunch = false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.clearButton.visibility = clearButtonVisibility(s)
                    searchQuery = s.toString()
                    searchRequestDebounce(searchQuery!!)
                } else if (s.isNullOrEmpty()) {
                    if (!isTextProgrammaticChange) {
                        clearRequest()
                    }
                }
            }
        }

        binding.searchHint.addTextChangedListener(simpleTextWatcher)

        binding.clearSearchHistory.setOnClickListener() {
            viewModel.onClickSearchClear()
        }

        binding.clearButton.setOnClickListener {
            clearRequest()
        }

        binding.searchUpdate.setOnClickListener() {
            viewModel.searchRequestUpdate(searchQuery!!)
        }
    }

    private fun clearRequest() {
        isTextProgrammaticChange = true
        hideKeyboard(requireContext(), binding.clearButton)
        binding.searchHint.setText(STR_DEF)
        viewModel.clickOnClearSearchRequest()
        isTextProgrammaticChange = false
    }

    private fun emptyUi() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.youSearchedIt.visibility = View.GONE
        binding.clearSearchHistory.visibility = View.GONE
        binding.searchPlaceholder.visibility = View.GONE
    }

    private fun loadingUi() {
        emptyUi()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun contentUi(content: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.youSearchedIt.visibility = View.GONE
        searchAdapter.tracks = content
        searchAdapter.notifyDataSetChanged()
        binding.clearSearchHistory.visibility = View.GONE
        binding.searchPlaceholder.visibility = View.GONE
        binding.youSearchedIt.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun historyUi(history: List<Track>) {
        binding.progressBar.visibility = View.GONE
        searchAdapter.tracks = history
        searchAdapter.notifyDataSetChanged()
        binding.youSearchedIt.visibility = View.VISIBLE
        binding.clearSearchHistory.visibility = View.VISIBLE
        binding.searchPlaceholder.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.clearButton.visibility = View.GONE
    }

    private fun networkErrorUi() {
        binding.progressBar.visibility = View.GONE
        binding.youSearchedIt.visibility = View.GONE
        binding.clearSearchHistory.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.searchUpdate.visibility = View.VISIBLE
        binding.errorImagePlaceholder.setImageDrawable(requireContext().getDrawable(R.drawable.network_error))
        binding.errorStatus.setText(R.string.network_error)
    }

    private fun tracksNotFoundUi() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.searchUpdate.visibility = View.GONE
        binding.clearSearchHistory.visibility = View.GONE
        binding.youSearchedIt.visibility = View.GONE
        binding.errorImagePlaceholder.setImageDrawable(requireContext().getDrawable(R.drawable.tracks_not_found))
        binding.errorStatus.setText(R.string.tracks_not_found)
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchRequestDebounce(query: String) {
        if (searchQuery == query && !searchQuery.isNullOrEmpty()) {
            viewModel.searchRequestDebounce(query)
        } else return
    }

    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val STR_DEF: String = ""
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}