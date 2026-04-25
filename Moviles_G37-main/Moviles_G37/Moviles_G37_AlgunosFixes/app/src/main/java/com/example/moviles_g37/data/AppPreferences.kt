package com.example.moviles_g37.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


object AppPreferences {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()


    fun load() {
        scope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val doc = db.collection("users").document(uid)
                    .collection("preferences").document("current")
                    .get().await()

                if (doc.exists()) {
                    val loaded = UserPreferences(
                        is3DBuildingsEnabled = doc.getBoolean("is3DBuildingsEnabled") ?: true,
                        isIndoorNavigationEnabled = doc.getBoolean("isIndoorNavigationEnabled") ?: true,
                        isAccessibleRoutesEnabled = doc.getBoolean("isAccessibleRoutesEnabled") ?: false,
                        walkingSpeed = doc.getString("walkingSpeed") ?: "Normal"
                    )
                    _preferences.value = loaded
                    Log.d("AppPreferences", "Preferences loaded: $loaded")
                }
            } catch (e: Exception) {
                Log.e("AppPreferences", "Error loading preferences: ${e.message}")
            }
        }
    }

    fun update(block: (UserPreferences) -> UserPreferences) {
        val newPrefs = block(_preferences.value)
        _preferences.value = newPrefs
        persist(newPrefs)
    }

    private fun persist(prefs: UserPreferences) {
        scope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val data = hashMapOf(
                    "is3DBuildingsEnabled" to prefs.is3DBuildingsEnabled,
                    "isIndoorNavigationEnabled" to prefs.isIndoorNavigationEnabled,
                    "isAccessibleRoutesEnabled" to prefs.isAccessibleRoutesEnabled,
                    "walkingSpeed" to prefs.walkingSpeed
                )
                db.collection("users").document(uid)
                    .collection("preferences").document("current")
                    .set(data).await()
                Log.d("AppPreferences", "Preferences saved")
            } catch (e: Exception) {
                Log.e("AppPreferences", "Error saving preferences: ${e.message}")
            }
        }
    }
}
