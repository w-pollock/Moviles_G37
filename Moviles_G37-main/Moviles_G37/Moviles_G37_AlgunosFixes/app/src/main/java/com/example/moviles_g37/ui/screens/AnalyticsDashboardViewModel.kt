package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.analytics.BQAnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsDashboardUiState(
    val isLoading: Boolean = true,
    val recentEvents: List<String> = emptyList(),
    // BQ 2 – Top searched places
    val topSearchedPlaces: List<Pair<String, Long>> = emptyList(),
    // BQ 9 – Map vs Search frequency
    val mapViewCount: Long = 0L,
    val searchUseCount: Long = 0L,
    // BQ 10 – Most favorited places
    val topFavoritedPlaces: List<Pair<String, Long>> = emptyList(),
    // BQ 11 – Average session duration
    val avgSessionDurationMs: Long = 0L
)

class AnalyticsDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsDashboardUiState())
    val uiState: StateFlow<AnalyticsDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun refresh() = loadData()

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val recentEvents = AppAnalytics.eventLog
                .takeLast(20)
                .reversed()
                .map { "[${it.name}] ${it.params.entries.take(2).joinToString(", ") { e -> "${e.key}=${e.value}" }}" }

            // BQ 2
            val topSearched = BQAnalyticsRepository.getTopSearchedPlaces()
            // BQ 9
            val (mapViews, searchUses) = BQAnalyticsRepository.getMapVsSearchUsage()
            // BQ 10
            val topFavorited = BQAnalyticsRepository.getMostFavoritedPlaces()
            // BQ 11
            val avgSession = BQAnalyticsRepository.getAverageSessionDurationMs()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    recentEvents = recentEvents,
                    topSearchedPlaces = topSearched,
                    mapViewCount = mapViews,
                    searchUseCount = searchUses,
                    topFavoritedPlaces = topFavorited,
                    avgSessionDurationMs = avgSession
                )
            }
        }
    }
}
