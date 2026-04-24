package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.PlaceInfo
import com.example.moviles_g37.data.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuickActionUiState(
    val category: String = "",
    val places: List<PlaceInfo> = emptyList(),
    val isLoading: Boolean = true
)

class QuickActionViewModel(
    private val category: String
) : ViewModel() {

    private val placesRepository = PlacesRepository()

    private val _uiState = MutableStateFlow(QuickActionUiState(category = category))
    val uiState: StateFlow<QuickActionUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onPlaceClicked(place: PlaceInfo) {
        AppAnalytics.track(
            event = "place_viewed",
            params = mapOf(
                "source_screen" to "quick_action_$category",
                "place_id" to place.id,
                "place_name" to place.name
            )
        )
    }

    private fun load() {
        viewModelScope.launch {
            val places = placesRepository.getPlacesByCategory(category)
            _uiState.update { it.copy(places = places, isLoading = false) }
        }
    }

    class Factory(private val category: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuickActionViewModel(category) as T
        }
    }
}
