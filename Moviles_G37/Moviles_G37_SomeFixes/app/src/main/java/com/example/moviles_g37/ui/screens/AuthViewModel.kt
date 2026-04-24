package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Tries to sign in. If the account doesn't exist, creates it (mirrors the
     * behaviour of the previous Settings flow).
     */
    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            AppAnalytics.track(
                "auth_attempt",
                mapOf("provider" to "email", "mode" to "sign_in")
            )
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                onAuthSucceeded(provider = "email", isNewAccount = false)
            } catch (signInEx: Exception) {
                // Try to create the account instead
                try {
                    auth.createUserWithEmailAndPassword(email.trim(), password).await()
                    onAuthSucceeded(provider = "email", isNewAccount = true)
                } catch (createEx: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = createEx.localizedMessage
                                ?: signInEx.localizedMessage
                                ?: "Authentication failed"
                        )
                    }
                    AppAnalytics.track(
                        "flow_abandoned",
                        mapOf(
                            "flow_name" to "auth",
                            "step" to "sign_in_failed",
                            "error" to (createEx.localizedMessage ?: "unknown")
                        )
                    )
                }
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            AppAnalytics.track("auth_attempt", mapOf("provider" to "anonymous"))
            try {
                auth.signInAnonymously().await()
                onAuthSucceeded(provider = "anonymous", isNewAccount = true)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Could not continue as guest"
                    )
                }
            }
        }
    }

    fun consumeAuthSuccess() {
        _uiState.update { it.copy(authSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun onAuthSucceeded(provider: String, isNewAccount: Boolean) {
        AppAnalytics.track(
            "auth_success",
            mapOf("provider" to provider, "new_account" to isNewAccount)
        )
        // Load user-specific preferences into the shared StateFlow.
        AppPreferences.load()
        _uiState.update { it.copy(isLoading = false, authSuccess = true, errorMessage = null) }
    }
}
