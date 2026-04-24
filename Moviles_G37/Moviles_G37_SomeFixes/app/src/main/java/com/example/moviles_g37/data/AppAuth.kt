package com.example.moviles_g37.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Reactive wrapper around FirebaseAuth that exposes the current user state
 * through a StateFlow. ViewModels observe this to gate personal content
 * (schedule, favorites, classroom calendar) on whether the user is a real
 * authenticated account or just a guest / no session.
 */
data class AuthState(val user: FirebaseUser? = null) {

    /** True if any session exists — anonymous or real. */
    val hasSession: Boolean get() = user != null

    /** True if the user is a guest (anonymous) or not signed in at all. */
    val isAnonymous: Boolean get() = user?.isAnonymous ?: true

    /** True iff the user has signed in with real credentials. */
    val isAuthenticated: Boolean get() = user != null && user.isAnonymous == false

    val email: String? get() = user?.email
    val uid: String? get() = user?.uid
}

object AppAuth {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState(firebaseAuth.currentUser))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { a ->
            _authState.value = AuthState(a.currentUser)
        }
    }

    val currentState: AuthState get() = _authState.value
}
