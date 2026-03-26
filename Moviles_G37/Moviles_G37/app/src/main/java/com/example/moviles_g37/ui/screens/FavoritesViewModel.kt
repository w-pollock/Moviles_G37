package com.example.moviles_g37.ui.screens

import android.widget.Filter
import androidx.lifecycle.ViewModel
import com.example.moviles_g37.analytics.AppAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FavoritePlace(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String
)

data class FavoriteUiState(
    val searchText: String = "",
    val selectedFilter: String = "All",
    val places: List<FavoritePlace> = defaultPlaces()
)


private fun defaultPlaces() = listOf(
    FavoritePlace("biblioteca", "Biblioteca ML", "Study & Research Place", "Academic"),
    FavoritePlace("ml", "Edificio Mario Laserna", "Engineering Faculty", "Academic"),
    FavoritePlace("cafeteria", "Cafeteria O", "Eating & Social Place", "Food"),
    FavoritePlace("caneca", "Centro Deportivo", "Sports & Recreation", "Sports")
)


class FavoritesViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        AppAnalytics.track(event = "screen_view", params = mapOf("screen_view" to "favorites"))

    }


    fun onSearchTextChanged(text: String){
        _uiState.update { it.copy(searchText = text) }
    }
    fun onFilterSelected(filter: String){
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onPlaceGoClicked(place: FavoritePlace){
        AppAnalytics.track(
            event = "favorite_opened",
            params = mapOf("place_name" to place.title, "category" to place.category)
        )
    }

    fun onNavigateToClassesClicked() {
        AppAnalytics.track(
            event = "route_started",
            params = mapOf("origin_screen" to "favorites", "destination" to "classes")
        )
    }

}
