package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.w3c.dom.Text

data class SearchResult(val id: String, val name: String, val category: String)

data class SearchUiState(
    val searchText: String = "Mario Laserna",
    val results: List<SearchResult> = defaultResults(),
    val recentSearches: List<String> = listOf("ML-603", "Chick n' Chips", "SD-803", "O-202"),
    val isSearchActive: Boolean = false
)


private fun defaultResults() = listOf(
    SearchResult("ml-building", "ML Building", "building"),
    SearchResult("ml-library", "ML Library", "study"),
    SearchResult("coffee-shop", "Coffee Shop", "food"),
    SearchResult("study-room", "Study Room", "study")

)

class SearchViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    private var searchStartTime: Long = 0L
    private var abandonmentJob: Job? = null

    init {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "search")
        )
    }

    fun onSearchTextChanged(text: String){
        _uiState.update { it.copy(searchText = text, isSearchActive = text.isNotEmpty()) }
        if (text.isNotEmpty()) {
            if (searchStartTime == 0L) searchStartTime = System.currentTimeMillis()
            AppAnalytics.track(event = "search_submitted", params = mapOf("query" to text))
            scheduleAbandonmentCheck()
        } else {
            cancelAbandonmentCheck()
            searchStartTime = 0L
        }
    }

    fun onResultClicked(result: SearchResult){
         cancelAbandonmentCheck()
        searchStartTime = 0L
        AppAnalytics.track(
            event = "place_viewed",
            params = mapOf("source_screen" to "search", "place_name" to result.name)
        )
    }

    fun onScreenAbandoned(){
        if (_uiState.value.isSearchActive && searchStartTime > 0L){
            AppAnalytics.track(
                event = "flow_abandoned",
                params = mapOf(
                    "flow_name"  to "search",
                    "step" to "results_shown",
                    "time_spent_ms" to (System.currentTimeMillis() - searchStartTime),
                    "query" to _uiState.value.searchText
                )
            )
        }

        cancelAbandonmentCheck()
        searchStartTime = 0L
    }

    private fun scheduleAbandonmentCheck(){
        cancelAbandonmentCheck()
        abandonmentJob = viewModelScope.launch {
            delay(30_000L)
            if (_uiState.value.isSearchActive) {
                AppAnalytics.track(
                    event = "flow_abandoned",
                    params = mapOf(
                        "flow_name" to "search",
                        "step" to "idle_after_search",
                        "query" to _uiState.value.searchText
                    )
                )
            }
        }
    }


    private fun cancelAbandonmentCheck(){
        abandonmentJob?.cancel()
        abandonmentJob = null
    }

    override fun onCleared() {
        super.onCleared()
        onScreenAbandoned()
    }


}