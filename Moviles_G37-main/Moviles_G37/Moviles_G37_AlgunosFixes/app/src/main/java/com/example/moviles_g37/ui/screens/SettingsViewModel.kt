package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.AppPreferences
import com.example.moviles_g37.data.UserPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val currentUser: FirebaseUser? = null,
    val isGuest: Boolean = true,
    val isSyncing: Boolean = false,
    val syncSuccess: Boolean = false,
    val signedOut: Boolean = false
)

class SettingsViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            currentUser = AppAuth.currentState.user,
            isGuest = AppAuth.currentState.isAnonymous,
            preferences = AppPreferences.preferences.value
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeAuth()
        observePreferences()
    }

    private fun observeAuth() {
        viewModelScope.launch {
            AppAuth.authState.collect { state ->
                _uiState.update {
                    it.copy(currentUser = state.user, isGuest = state.isAnonymous)
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            AppPreferences.preferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }




    fun on3DBuildingsToggled(enabled: Boolean) {
        AppPreferences.update { it.copy(is3DBuildingsEnabled = enabled) }
        AppAnalytics.track(
            "setting_changed",
            mapOf("setting_name" to "3d_buildings", "value" to enabled)
        )
    }

    fun onIndoorNavigationToggled(enabled: Boolean) {
        AppPreferences.update { it.copy(isIndoorNavigationEnabled = enabled) }
        AppAnalytics.track(
            "setting_changed",
            mapOf("setting_name" to "indoor_navigation", "value" to enabled)
        )
    }

    fun onAccessibleRoutesToggled(enabled: Boolean) {
        AppPreferences.update { it.copy(isAccessibleRoutesEnabled = enabled) }
        AppAnalytics.track(
            "setting_changed",
            mapOf("setting_name" to "accessible_routes", "value" to enabled)
        )
    }

    fun onWalkingSpeedSelected(speed: String) {
        AppPreferences.update { it.copy(walkingSpeed = speed) }
        AppAnalytics.track(
            "setting_changed",
            mapOf("setting_name" to "walking_speed", "value" to speed)
        )
    }




    fun onSignInRequested() {
        AppAnalytics.track(
            "sign_in_prompt_clicked",
            mapOf("source" to "settings_card", "was_guest" to _uiState.value.isGuest)
        )
        auth.signOut()
        _uiState.update { it.copy(signedOut = true) }
    }

    fun onSignOutRequested() {
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "sign_out"))
        auth.signOut()
        _uiState.update { it.copy(signedOut = true) }
    }

    fun consumeSignedOut() {
        _uiState.update { it.copy(signedOut = false) }
    }

    fun onSyncClicked() {
        if (_uiState.value.isGuest) {
            AppAnalytics.track(
                "flow_abandoned",
                mapOf("flow_name" to "sync", "step" to "not_authenticated")
            )
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            AppAnalytics.track("settings_sync_clicked", mapOf("provider" to "uniandes_account"))
            kotlinx.coroutines.delay(1500)
            _uiState.update { it.copy(isSyncing = false, syncSuccess = true) }
        }
    }
}
