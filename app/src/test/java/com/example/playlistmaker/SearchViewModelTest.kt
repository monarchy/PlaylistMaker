package com.example.playlistmaker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

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
    fun `init with empty history sets EmptyScreen`() {
        every { getSetInteractor.getSearchHistory() } returns mutableListOf()

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        assertEquals(SearchState.EmptyScreen, viewModel.screenState.value)
    }

    @Test
    fun `init with track history sets ShowHistoryContent`() {
        val track = sampleTrack()
        every { getSetInteractor.getSearchHistory() } returns mutableListOf(track)

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        assertEquals(SearchState.ShowHistoryContent(listOf(track)), viewModel.screenState.value)
    }

    @Test
    fun `searchRequestDebounce with results emits ShowSearchContent`() = runTest(testDispatcher) {
        val track = sampleTrack()
        every { getSetInteractor.getSearchHistory() } returns mutableListOf()
        coEvery { searchInteractor.searchTracks("test") } returns flow {
            emit(Pair(listOf(track), null))
        }

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        viewModel.searchRequestDebounce("test")
        advanceTimeBy(2100)

        assertEquals(SearchState.ShowSearchContent(listOf(track)), viewModel.screenState.value)
    }

    @Test
    fun `searchRequestDebounce with error emits Error state`() = runTest(testDispatcher) {
        val error = SearchState.NetworkError
        every { getSetInteractor.getSearchHistory() } returns mutableListOf()
        coEvery { searchInteractor.searchTracks("fail") } returns flow {
            emit(Pair(null, error))
        }

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)

        viewModel.searchRequestDebounce("fail")
        advanceTimeBy(2100)

        assertEquals(error, viewModel.screenState.value)
    }

    @Test
    fun `onClickSearchClear clears history and sets EmptyScreen`() {
        val preferences = mockk<android.content.SharedPreferences>(relaxed = true)
        every { getSetInteractor.getPreferences() } returns preferences

        viewModel = SearchViewModel(getSetInteractor, cleanerInteractor, searchInteractor)
        viewModel.onClickSearchClear()

        verify { cleanerInteractor.execute() }
        assertEquals(SearchState.EmptyScreen, viewModel.screenState.value)
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