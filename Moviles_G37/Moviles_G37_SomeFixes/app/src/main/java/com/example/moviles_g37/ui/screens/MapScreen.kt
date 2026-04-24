package com.example.moviles_g37.ui.screens

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    onNavigateToLocationDetails: (String) -> Unit,
    mapViewModel: MapViewModel = viewModel()
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "map"))
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UNIANDES_LAT_LNG, 17f)
    }

    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f))
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) mapViewModel.onLocationPermissionGranted()
        else mapViewModel.onLocationPermissionDenied()
    }

    LaunchedEffect(Unit) {
        if (!uiState.hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    val mapHeight = if (isLandscape) 260.dp else 400.dp

    // Root scroll so content never gets cut off in landscape.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SenecaYellow)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow).padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SenecaHeader(title = "SénecaMaps")
            Spacer(modifier = Modifier.height(18.dp))
            SenecaSearchBar(
                value = uiState.searchText,
                onValueChange = mapViewModel::onSearchTextChanged,
                placeholder = "Search building or room..."
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = uiState.hasLocationPermission,
                isBuildingEnabled = uiState.preferences.is3DBuildingsEnabled,
                isIndoorEnabled = uiState.preferences.isIndoorNavigationEnabled
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = uiState.hasLocationPermission
            )
        ) {
            uiState.markers.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.name,
                    snippet = marker.description.ifBlank { marker.building },
                    onClick = {
                        mapViewModel.onMarkerClicked(marker)
                        false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val cardMarker = uiState.selectedMarker ?: uiState.markers.firstOrNull()
        if (cardMarker != null) {
            LocationSummaryCard(
                title = cardMarker.name,
                subtitle = cardMarker.description.ifBlank { cardMarker.building },
                onClick = {
                    mapViewModel.onStartNavigation(cardMarker)
                    onNavigateToLocationDetails(cardMarker.id)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LocationSummaryCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).background(Graphite)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(text = title, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            text = subtitle, color = White.copy(alpha = 0.75f), fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onClick, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DimGrey, contentColor = White)
        ) {
            Icon(imageVector = Icons.Outlined.Navigation, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "View Details", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
