package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import com.example.moviles_g37.analytics.AnalyticsEvent
import com.example.moviles_g37.analytics.AnalyticsObserver
import com.example.moviles_g37.analytics.AppAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DashBoardUiState(
    val recentEvents: List<AnalyticsEvent> = emptyList(),
    val eventCounts: Map<String, Int> = emptyMap()
)

class AnalyticsDashboardViewModel: ViewModel(), AnalyticsObserver {
    private val _uiState = MutableStateFlow(
        DashBoardUiState(
            recentEvents = AppAnalytics.eventLog,
            eventCounts = AppAnalytics.eventLog
                .groupingBy { it.name }
                .eachCount()
        )
    )

    val uiState: StateFlow<DashBoardUiState> = _uiState.asStateFlow()

    init {
        AppAnalytics.addObserver(this)
    }

    override fun onEventTracked(event: AnalyticsEvent){
        _uiState.update { current ->
            val updatedEvents = current.recentEvents + event
            val updatedCounts = updatedEvents
                .groupingBy { it.name }
                .eachCount()
            current.copy(
                recentEvents = updatedEvents,
                eventCounts = updatedCounts
            )
        }
    }

    override fun onCleared(){
        super.onCleared()
        AppAnalytics.removeObserver(this)
    }
}