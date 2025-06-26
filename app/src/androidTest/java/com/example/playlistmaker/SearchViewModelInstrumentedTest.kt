package com.example.playlistmaker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchTrackInteractor
import com.example.playlistmaker.domain.search.StorageGetSetInterractor
import com.example.playlistmaker.domain.search.StoreCleanerInterractor
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.util.SearchState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelInstrumentedTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getSetInteractor = mockk<StorageGetSetInterractor>(relaxed = true)
    private val cleanerInteractor = mockk<StoreCleanerInterractor>(relaxed = true)
    private val searchInteractor = mockk<SearchTrackInteractor>(relaxed = true)

    private lateinit var viewModel: SearchViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emptyHistorySetsEmptyScreen() {
        every { getSetInteractor.getSearchHistory() } returns mutableListOf()

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        Assert.assertEquals(SearchState.EmptyScreen, viewModel.screenState.value)
    }

    @Test
    fun nonEmptyHistorySetsShowHistoryContent() {
        val track = sampleTrack()
        every { getSetInteractor.getSearchHistory() } returns mutableListOf(track)

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        Assert.assertEquals(SearchState.ShowHistoryContent(listOf(track)), viewModel.screenState.value)
    }

    @Test
    fun onClickSearchClearClearsHistory() {
        val sharedPrefs = mockk<android.content.SharedPreferences>(relaxed = true)
        every { getSetInteractor.getPreferences() } returns sharedPrefs

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)
        viewModel.onClickSearchClear()

        verify { cleanerInteractor.execute() }
        Assert.assertEquals(SearchState.EmptyScreen, viewModel.screenState.value)
    }

    @Test
    fun searchRequestDebounceSetsShowSearchContent() = runTest(testDispatcher) {
        val track = sampleTrack()
        val expectedList = listOf(track)

        every { getSetInteractor.getSearchHistory() } returns mutableListOf()
        coEvery { searchInteractor.searchTracks("test") } returns flow {
            emit(Pair(expectedList, null))
        }

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)
        viewModel.searchRequestDebounce("test")

        advanceTimeBy(2100)

        Assert.assertEquals(SearchState.ShowSearchContent(expectedList), viewModel.screenState.value)
    }

    @Test
    fun searchRequestDebounceSetsNetworkError() = runTest(testDispatcher) {
        val errorState = SearchState.NetworkError

        every { getSetInteractor.getSearchHistory() } returns mutableListOf()
        coEvery { searchInteractor.searchTracks("test") } returns flow {
            emit(Pair(null, errorState))
        }

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)
        viewModel.searchRequestDebounce("test")

        advanceTimeBy(2100)

        Assert.assertEquals(errorState, viewModel.screenState.value)
    }

    private fun sampleTrack() = Track(
        trackName = "Test",
        artistName = "Artist",
        trackTimeMillis = 10000,
        artworkUrl100 = "http://example.com/image.jpg",
        collectionName = "Album",
        releaseDate = "2023",
        primaryGenreName = "Genre",
        country = "Country",
        previewUrl = "http://example.com/preview.mp3"
    )
}