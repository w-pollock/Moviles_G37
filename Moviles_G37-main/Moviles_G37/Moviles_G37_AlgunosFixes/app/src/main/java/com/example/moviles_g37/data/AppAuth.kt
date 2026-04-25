package com.example.moviles_g37.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class AuthState(val user: FirebaseUser? = null) {


    val hasSession: Boolean get() = user != null


    val isAnonymous: Boolean get() = user?.isAnonymous ?: true


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
