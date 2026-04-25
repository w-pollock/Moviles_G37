package com.example.moviles_g37.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.PlaceInfo
import com.example.moviles_g37.data.PlacesRepository
import com.example.moviles_g37.data.RecentSearchesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchText: String = "",
    val results: List<PlaceInfo> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isGuest: Boolean = true
)

@OptIn(FlowPreview::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val placesRepository = PlacesRepository(application.applicationContext)
    private val recentSearchesRepository = RecentSearchesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Flow abandonment tracking
    private var searchSessionStartTime: Long = 0L
    private var lastQuery: String = ""
    private var searchResultedInClick: Boolean = false

    init {
        observeAuth()
        // Debounced live search
        viewModelScope.launch {
            _uiState
                .map { it.searchText }
                .distinctUntilChanged()
                .debounce(400L)
                .collectLatest { query -> performSearch(query) }
        }
    }

    fun onSearchTextChanged(query: String) {
        if (searchSessionStartTime == 0L && query.isNotBlank()) {
            searchSessionStartTime = System.currentTimeMillis()
            searchResultedInClick = false
            AppAnalytics.track("search_session_started", mapOf("source" to "search_screen"))
        }
        lastQuery = query
        _uiState.update { it.copy(searchText = query) }
    }

    fun onResultClicked(place: PlaceInfo) {
        searchResultedInClick = true
        val sessionDuration = if (searchSessionStartTime > 0L)
            System.currentTimeMillis() - searchSessionStartTime else 0L
        AppAnalytics.track(
            "place_viewed",
            mapOf(
                "source_screen" to "search",
                "place_id" to place.id,
                "place_name" to place.name,
                "query" to lastQuery,
                "session_duration_ms" to sessionDuration
            )
        )
        viewModelScope.launch {
            if (lastQuery.isNotBlank()) {
                recentSearchesRepository.addSearch(lastQuery)
                loadRecentSearches()
            }
        }
        searchSessionStartTime = 0L
    }

    /** Called by DisposableEffect onDispose — detects flow abandonment */
    fun onScreenAbandoned() {
        if (searchSessionStartTime > 0L && !searchResultedInClick && lastQuery.isNotBlank()) {
            val abandonDuration = System.currentTimeMillis() - searchSessionStartTime
            AppAnalytics.track(
                "flow_abandoned",
                mapOf(
                    "flow_name" to "search",
                    "query" to lastQuery,
                    "results_count" to _uiState.value.results.size,
                    "session_duration_ms" to abandonDuration
                )
            )
        }
        searchSessionStartTime = 0L
        searchResultedInClick = false
    }

    fun onRecentSearchClicked(query: String) {
        _uiState.update { it.copy(searchText = query) }
        AppAnalytics.track("recent_search_clicked", mapOf("query" to query))
    }

    /** Exposed for LaunchedEffect(Unit) in SearchScreen */
    fun refreshRecents() {
        loadRecentSearches()
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(results = emptyList(), isLoading = false) }
                loadRecentSearches()
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }
            val results = placesRepository.searchPlaces(query)
            AppAnalytics.track(
                "search_submitted",
                mapOf("query" to query, "results_count" to results.size)
            )
            _uiState.update { it.copy(results = results, isLoading = false) }
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            val searches = recentSearchesRepository.getRecentSearches(limit = 6)
            _uiState.update { it.copy(recentSearches = searches) }
        }
    }

    private fun observeAuth() {
        viewModelScope.launch {
            AppAuth.authState.collect { state ->
                _uiState.update { it.copy(isGuest = state.isAnonymous) }
                loadRecentSearches()
            }
        }
    }
}
