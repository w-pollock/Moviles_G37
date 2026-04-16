package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritePlace(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String
)

data class FavoriteUiState(
    val searchText: String = "",
    val selectedFilter: String = "All",
    val places: List<FavoritePlace> = emptyList(),
    val isLoading: Boolean = false
)

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "favorites")
        )
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val favorites = repository.getFavorites()
            if (favorites.isEmpty()) {
                val defaults = defaultPlaces()
                defaults.forEach { place ->
                    repository.addFavorite(place)
                }
                _uiState.update { it.copy(places = defaults, isLoading = false) }
            } else {
                _uiState.update { it.copy(places = favorites, isLoading = false) }
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onPlaceGoClicked(place: FavoritePlace) {
        AppAnalytics.track(
            event = "favorite_opened",
            params = mapOf(
                "place_name" to place.title,
                "category" to place.category
            )
        )
    }

    fun onNavigateToClassesClicked() {
        AppAnalytics.track(
            event = "route_started",
            params = mapOf(
                "origin_screen" to "favorites",
                "destination" to "classes"
            )
        )
    }

    fun addFavorite(place: FavoritePlace) {
        viewModelScope.launch {
            repository.addFavorite(place)
            loadFavorites()
        }
    }

    fun removeFavorite(placeId: String) {
        viewModelScope.launch {
            repository.removeFavorite(placeId)
            loadFavorites()
        }
    }
}

private fun defaultPlaces() = listOf(
    FavoritePlace("biblioteca", "Biblioteca ML", "Study & Research Place", "Academic"),
    FavoritePlace("ml", "Edificio Mario Laserna", "Engineering Faculty", "Academic"),
    FavoritePlace("cafeteria", "Cafeteria O", "Eating & Social Place", "Food"),
    FavoritePlace("caneca", "Centro Deportivo", "Sports & Recreation", "Sports")
)