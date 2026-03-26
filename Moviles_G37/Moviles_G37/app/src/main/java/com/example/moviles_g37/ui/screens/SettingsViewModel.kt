package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SettingsUiState(
    val is3DBuildingsEnabled: Boolean = true,
    val isIndoorNavigationEnabled: Boolean = true,
    val isAccessibleRoutesEnabled: Boolean = false,
    val selectedWalkingSpeed: String = "Normal",
    val currentUser: FirebaseUser? = null,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isSyncing: Boolean = false,
    val syncSuccess: Boolean = false
)


class SettingsViewModel: ViewModel() {
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(SettingsUiState(currentUser = auth.currentUser))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        AppAnalytics.track(event = "screen_view", params = mapOf("screen_name" to "settings"))
        auth.addAuthStateListener { firebaseAuth ->
            _uiState.update { it.copy(currentUser = firebaseAuth.currentUser) }
        }
    }

    fun on3DBuildingsToggled(enabled: Boolean){
        _uiState.update { it.copy(is3DBuildingsEnabled = enabled) }
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "3d_buildings", "value" to enabled))
    }

    fun onIndoorNavigationToggled(enabled: Boolean){
        _uiState.update { it.copy(isIndoorNavigationEnabled = enabled) }
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "indoor_navigation", "value" to enabled))

    }

    fun onAccessibleRoutesToggled(enabled: Boolean){
        _uiState.update { it.copy(isAccessibleRoutesEnabled = enabled) }
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "accessible_routes", "value" to enabled))
    }

    fun onWalkingSpeedSelected(speed: String){
        _uiState.update { it.copy(selectedWalkingSpeed = speed) }
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "walking_speed", "value" to speed))
    }


    fun signInWithEmail(email: String, password: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null) }
            AppAnalytics.track("settings_sync_clicked", mapOf("provider" to "email"))
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.update { it.copy(isAuthLoading = false) }
            } catch (e: Exception){
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    _uiState.update { it.copy(isAuthLoading = false) }
                } catch (createEx: Exception){
                    _uiState.update { it.copy(isAuthLoading = false, authError = createEx.localizedMessage) }
                    AppAnalytics.track("flow_abandoned", mapOf(
                        "flow_name" to "auth",
                        "step" to "sign_in_failed",
                        "error" to (createEx.localizedMessage ?: "unknown")
                    ))
                }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null) }
            try {
                auth.signInAnonymously().await()
                _uiState.update { it.copy(isAuthLoading = false) }
                AppAnalytics.track("settings_sync_clicked", mapOf("provider" to "anonymous"))

            } catch (e: Exception){
                _uiState.update { it.copy(isAuthLoading = false, authError = e.localizedMessage) }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        AppAnalytics.track("setting_changed", mapOf("setting_name" to "sign_out"))
    }

    fun onSyncClicked(){
        if (_uiState.value.currentUser == null){
            AppAnalytics.track("flow_abandoned", mapOf("flow_name" to "sync", "step" to "not_authenticated"))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            AppAnalytics.track("settings_sync_clicked", mapOf("provider" to "uniandes_account"))
            kotlinx.coroutines.delay(1500   )
            _uiState.update { it.copy(isSyncing = false, syncSuccess = true) }
        }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }
}

