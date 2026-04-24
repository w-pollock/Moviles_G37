package com.example.moviles_g37.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppPreferences
import com.example.moviles_g37.data.PlaceInfo
import com.example.moviles_g37.data.PlacesRepository
import com.example.moviles_g37.data.UserPreferences
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val UNIANDES_LAT_LNG = LatLng(4.6016, -74.0657)

data class MapUiState(
    val searchText: String = "",
    val userLocation: LatLng? = null,
    val hasLocationPermission: Boolean = false,
    val isNavigating: Boolean = false,
    val selectedMarker: PlaceInfo? = null,
    val routeStartTime: Long? = null,
    val markers: List<PlaceInfo> = emptyList(),
    val preferences: UserPreferences = UserPreferences()
)

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val placesRepository = PlacesRepository()

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        // Seed permission state based on what the system already granted.
        _uiState.update { it.copy(hasLocationPermission = currentlyHasPermission()) }
        observePreferences()
        loadPlaces()
        if (currentlyHasPermission()) fetchUserLocation()
    }

    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onLocationPermissionGranted() {
        // BUG FIX: previously set hasLocationPermission = false.
        _uiState.update { it.copy(hasLocationPermission = true) }
        fetchUserLocation()
    }

    fun onLocationPermissionDenied() {
        _uiState.update { it.copy(hasLocationPermission = false) }
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        if (!currentlyHasPermission()) return
        viewModelScope.launch {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
                if (location != null) {
                    _uiState.update {
                        it.copy(userLocation = LatLng(location.latitude, location.longitude))
                    }
                }
            } catch (e: Exception) {
                // Permission may have been revoked between check and call; ignore.
            }
        }
    }

    fun onMarkerClicked(marker: PlaceInfo) {
        _uiState.update { it.copy(selectedMarker = marker) }
        AppAnalytics.track(
            event = "place_viewed",
            params = mapOf(
                "source_screen" to "map",
                "place_name" to marker.name,
                "place_id" to marker.id
            )
        )
    }

    fun onStartNavigation(destination: PlaceInfo) {
        _uiState.update {
            it.copy(isNavigating = true, routeStartTime = System.currentTimeMillis())
        }
        AppAnalytics.track(
            event = "route_started",
            params = mapOf(
                "origin_screen" to "map",
                "destination" to destination.name,
                "place_id" to destination.id
            )
        )
    }

    fun onRouteCompleted(destination: PlaceInfo) {
        val duration =
            _uiState.value.routeStartTime?.let { System.currentTimeMillis() - it } ?: 0L
        _uiState.update { it.copy(isNavigating = false, routeStartTime = null) }
        AppAnalytics.track(
            event = "route_completed",
            params = mapOf(
                "destination" to destination.name,
                "place_id" to destination.id,
                "duration_ms" to duration  // fixed key (was "duration.ms")
            )
        )
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            val places = placesRepository.getPlaces()
            _uiState.update { it.copy(markers = places) }
        }
    }

    /** Mirror AppPreferences changes into the UI state so Settings toggles take effect live. */
    private fun observePreferences() {
        viewModelScope.launch {
            AppPreferences.preferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    private fun currentlyHasPermission(): Boolean {
        val ctx = getApplication<Application>()
        val fine = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
}
