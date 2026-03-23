package com.example.moviles_g37.navigation

sealed class Screen(val route: String, val title: String) {
    data object Home: Screen("home", "Home")
    data object Search: Screen("search", "Search")
    data object Map: Screen("map", "Map")
    data object Favorites: Screen("favorites", "Favorites")
    data object LocationDetails: Screen("location_details", "Location_details")
    data object Settings: Screen("settings", "Settings")
}