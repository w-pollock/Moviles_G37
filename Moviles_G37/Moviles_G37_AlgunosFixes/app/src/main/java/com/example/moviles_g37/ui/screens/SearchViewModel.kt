package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.PlaceInfo
import com.example.moviles_g37.data.PlacesRepository
import com.example.moviles_g37.data.RecentSearchesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchText: String = "",
    val allPlaces: List<PlaceInfo> = emptyList(),
    val results: List<PlaceInfo> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearchActive: Boolean = false,
    val didSelectResult: Boolean = false
)

class SearchViewModel : ViewModel() {

    private val placesRepository = PlacesRepository()
    private val recentSearchesRepository = RecentSearchesRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchStartTime: Long = 0L
    private var abandonmentJob: Job? = null
    private var lastAbandonedQuery: String? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val places = placesRepository.getPlaces()
            val recent = recentSearchesRepository.getRecentSearches()
            _uiState.update {
                it.copy(allPlaces = places, recentSearches = recent)
            }
        }
    }

    fun refreshRecents() {
        viewModelScope.launch {
            val recent = recentSearchesRepository.getRecentSearches()
            _uiState.update { it.copy(recentSearches = recent) }
        }
    }

    fun onSearchTextChanged(text: String) {
        val newResults = if (text.isBlank()) emptyList()
        else _uiState.value.allPlaces.filter { it.matchesQuery(text) }

        _uiState.update {
            it.copy(
                searchText = text,
                results = newResults,
                isSearchActive = text.isNotEmpty(),
                didSelectResult = false
            )
        }

        if (text.isNotEmpty()) {
            if (searchStartTime == 0L) searchStartTime = System.currentTimeMillis()
            AppAnalytics.track("search_submitted", mapOf("query" to text))
            scheduleAbandonmentCheck()
        } else {
            cancelAbandonmentCheck()
            searchStartTime = 0L
        }
    }

    fun onResultClicked(result: PlaceInfo) {
        cancelAbandonmentCheck()
        searchStartTime = 0L
        _uiState.update { it.copy(didSelectResult = true) }

        AppAnalytics.track(
            event = "place_viewed",
            params = mapOf(
                "source_screen" to "search",
                "place_id" to result.id,
                "place_name" to result.name,
                "category" to result.category
            )
        )


        val q = _uiState.value.searchText
        if (q.isNotBlank()) {
            viewModelScope.launch {
                recentSearchesRepository.addSearch(q)
                refreshRecents()
            }
        }
    }

    fun onRecentSearchClicked(query: String) {
        onSearchTextChanged(query)
    }


    fun onScreenAbandoned() {
        val state = _uiState.value
        val q = state.searchText
        val alreadyLogged = q == lastAbandonedQuery
        if (state.isSearchActive && !state.didSelectResult && searchStartTime > 0L && !alreadyLogged) {
            AppAnalytics.track(
                event = "flow_abandoned",
                params = mapOf(
                    "flow_name" to "search",
                    "step" to "left_screen",
                    "time_spent_ms" to (System.currentTimeMillis() - searchStartTime),
                    "query" to q
                )
            )
            lastAbandonedQuery = q
        }
        cancelAbandonmentCheck()
        searchStartTime = 0L
    }

    private fun scheduleAbandonmentCheck() {
        cancelAbandonmentCheck()
        abandonmentJob = viewModelScope.launch {
            delay(30_000L)
            val state = _uiState.value
            val q = state.searchText
            if (state.isSearchActive && !state.didSelectResult && q != lastAbandonedQuery) {
                AppAnalytics.track(
                    event = "flow_abandoned",
                    params = mapOf(
                        "flow_name" to "search",
                        "step" to "idle_after_search",
                        "query" to q
                    )
                )
                lastAbandonedQuery = q
            }
        }
    }

    private fun cancelAbandonmentCheck() {
        abandonmentJob?.cancel()
        abandonmentJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelAbandonmentCheck()
    }
}
