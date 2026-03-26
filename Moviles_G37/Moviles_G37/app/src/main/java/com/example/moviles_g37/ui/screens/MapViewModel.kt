package com.example.moviles_g37.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val UNIANDES_LAT_LNG = LatLng(67.676, -67.67676)

data class MapMarker(
    val id: String,
    val position: LatLng,
    val title: String,
    val snippet: String
)

data class MapUiState(
    val searchText: String = "",
    val userLocation: LatLng? = null,
    val hasLocationPermission: Boolean = false,
    val isNavigating: Boolean = false,
    val selectedMarker: MapMarker? = null,
    val routeStartTime: Long? = null,
    val markers: List<MapMarker> = defaultMarkers()
)



private fun defaultMarkers() = listOf(
    MapMarker("ml-603", LatLng(4.456, 3.45667), "ML-603", "Mario Laserna - Piso 6"),
    MapMarker("biblioteca", LatLng(1.235, 4.56467), "Biblioteca ML", "Study & Research Place"),
    MapMarker("cafeteria", LatLng(5.45456, 10.9854), "Cafetería O", "Eating & Social Place"),
    MapMarker("caneca", LatLng(12.5465, 354.23134), "Centro Deportivo", "Sports & Recreation")
)

class MapViewModel(application: Application): AndroidViewModel(application){
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()


    init {
        AppAnalytics.track(event = "screen_view", params = mapOf("screen_name" to "map"))

    }


    fun onSearchTextChanged(text: String){
        _uiState.update { it.copy(searchText = text) }
    }

    fun onLocationPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = false) }
    }

    fun onLocationPermissionDenied() {
        _uiState.update { it.copy(hasLocationPermission = false) }
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation(){
        viewModelScope.launch {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()
                if (location != null){
                    _uiState.update {
                        it.copy(userLocation = LatLng(location.latitude, location.longitude))

                    }
                }
            } catch (e: Exception){

            }
        }
    }

    fun onMarkerClicked(marker: MapMarker){
        _uiState.update { it.copy(selectedMarker = marker) }
        AppAnalytics.track(
            event = "place_viewed",
            params = mapOf("source_screen" to "map", "place_name" to marker.title, "place_id" to marker.id)

        )
    }

    fun onStartNavigation(destination: MapMarker){
        _uiState.update { it.copy(isNavigating = true, routeStartTime = System.currentTimeMillis()) }
        AppAnalytics.track(
            event = "route_started",
            params = mapOf("origin_screen" to "map", "destination" to destination.title)
        )
    }


    fun onRouteCompleted(destination: MapMarker){
        val duration = _uiState.value.routeStartTime?.let { System.currentTimeMillis() - it } ?: 0L
        _uiState.update { it.copy(isNavigating = false, routeStartTime = null) }
        AppAnalytics.track(
            event = "route_completed",
            params = mapOf("destination"  to destination.title, "duration.ms" to duration)
        )
    }
}