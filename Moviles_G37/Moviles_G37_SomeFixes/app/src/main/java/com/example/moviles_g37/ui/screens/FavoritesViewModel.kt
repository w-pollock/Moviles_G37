package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.FavoritePlace
import com.example.moviles_g37.data.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val searchText: String = "",
    val selectedFilter: String = "All",
    val places: List<FavoritePlace> = emptyList(),
    val isGuest: Boolean = true,
    val isLoading: Boolean = true
)

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        observeAuth()
    }

    fun refresh() = loadFavorites()

    private fun observeAuth() {
        viewModelScope.launch {
            AppAuth.authState.collect { state ->
                _uiState.update { it.copy(isGuest = state.isAnonymous) }
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        if (AppAuth.currentState.isAnonymous) {
            _uiState.update { it.copy(places = emptyList(), isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val favorites = repository.getFavorites()
            _uiState.update { it.copy(places = favorites, isLoading = false) }
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
                "place_id" to place.id,
                "place_name" to place.title,
                "category" to place.category
            )
        )
    }

    fun onNavigateToClassesClicked() {
        AppAnalytics.track(
            event = "route_started",
            params = mapOf("origin_screen" to "favorites", "destination" to "classes")
        )
    }

    fun onSignInPromptClicked() {
        AppAnalytics.track("sign_in_prompt_clicked", mapOf("source" to "favorites_empty"))
    }

    fun removeFavorite(placeId: String) {
        viewModelScope.launch {
            repository.removeFavorite(placeId)
            loadFavorites()
        }
    }
}
