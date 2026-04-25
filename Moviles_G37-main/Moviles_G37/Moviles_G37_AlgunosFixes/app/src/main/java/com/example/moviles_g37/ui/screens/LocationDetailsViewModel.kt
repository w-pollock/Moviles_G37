package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.FavoritePlace
import com.example.moviles_g37.data.FavoritesRepository
import com.example.moviles_g37.data.PlaceInfo
import com.example.moviles_g37.data.PlacesRepository
import com.example.moviles_g37.data.ScheduleEntry
import com.example.moviles_g37.data.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class LocationDetailsUiState(
    val place: PlaceInfo? = null,
    val isGuest: Boolean = true,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val scheduleForThisRoom: List<ScheduleEntry> = emptyList(),
    val currentClass: ScheduleEntry? = null,
    val weeklySchedule: List<ScheduleEntry> = emptyList()
)

class LocationDetailsViewModel(
    private val placeId: String
) : ViewModel() {

    private val placesRepository = PlacesRepository()
    private val scheduleRepository = ScheduleRepository()
    private val favoritesRepository = FavoritesRepository()

    private val _uiState = MutableStateFlow(LocationDetailsUiState())
    val uiState: StateFlow<LocationDetailsUiState> = _uiState.asStateFlow()

    init {
        observeAuth()
    }

    fun toggleFavorite() {
        val state = _uiState.value
        val place = state.place ?: return
        if (state.isGuest) return

        viewModelScope.launch {
            if (state.isFavorite) {
                favoritesRepository.removeFavorite(place.id)
                _uiState.update { it.copy(isFavorite = false) }
                AppAnalytics.track(
                    "favorite_removed",
                    mapOf("place_id" to place.id, "place_name" to place.name)
                )
            } else {
                favoritesRepository.addFavorite(FavoritePlace.fromPlace(place))
                _uiState.update { it.copy(isFavorite = true) }
                AppAnalytics.track(
                    "favorite_added",
                    mapOf("place_id" to place.id, "place_name" to place.name)
                )
            }
        }
    }

    fun onSignInPromptClicked() {
        AppAnalytics.track("sign_in_prompt_clicked", mapOf("source" to "location_details"))
    }

    private fun observeAuth() {
        viewModelScope.launch {
            AppAuth.authState.collect { state ->
                _uiState.update { it.copy(isGuest = state.isAnonymous) }
                loadDetails()
            }
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            val isGuest = AppAuth.currentState.isAnonymous
            val place = placesRepository.getPlaceById(placeId)


            val fav = if (!isGuest && place != null) {
                favoritesRepository.isFavorite(place.id)
            } else false
            val fullSchedule = if (!isGuest) scheduleRepository.getSchedule() else emptyList()

            val roomMatches = if (place != null && !isGuest) {
                fullSchedule.filter { matchesRoom(it.room, place) }
            } else emptyList()

            val now = Calendar.getInstance()
            val today = now.get(Calendar.DAY_OF_WEEK)
            val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val currentClass = roomMatches.firstOrNull { e ->
                e.dayOfWeek == today && nowMinutes in e.startTotalMinutes..e.endTotalMinutes
            }

            if (place != null) {
                AppAnalytics.track(
                    "place_viewed",
                    mapOf(
                        "source_screen" to "location_details",
                        "place_id" to place.id,
                        "place_name" to place.name
                    )
                )
            }

            _uiState.update {
                it.copy(
                    place = place,
                    isFavorite = fav,
                    scheduleForThisRoom = roomMatches,
                    currentClass = currentClass,
                    weeklySchedule = fullSchedule,
                    isLoading = false
                )
            }
        }
    }

    private fun matchesRoom(room: String, place: PlaceInfo): Boolean {
        if (room.isBlank()) return false
        val normalized = room.trim().lowercase().replace(" ", "-")
        return normalized == place.id.lowercase() ||
                normalized == place.name.lowercase().replace(" ", "-")
    }

    class Factory(private val placeId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationDetailsViewModel(placeId) as T
        }
    }
}
