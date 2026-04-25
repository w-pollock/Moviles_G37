package com.example.moviles_g37.data

data class UserPreferences(
    val is3DBuildingsEnabled: Boolean = true,
    val isIndoorNavigationEnabled: Boolean = true,
    val isAccessibleRoutesEnabled: Boolean = false,
    val walkingSpeed: String = "Normal"  // "Slow" | "Normal" | "Fast"
)
