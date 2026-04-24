package com.example.moviles_g37.navigation

sealed class Screen(val route: String, val title: String) {
    data object Auth : Screen("auth", "Sign in")
    data object Home : Screen("home", "Home")
    data object Search : Screen("search", "Search")
    data object Map : Screen("map", "Map")
    data object Favorites : Screen("favorites", "Favorites")
    data object Settings : Screen("settings", "Settings")
    data object Analytics : Screen("analytics", "Analytics")

    // --- Screens with arguments --------------------------------------------
    data object LocationDetails : Screen("location_details/{placeId}", "Location details") {
        const val ARG_PLACE_ID = "placeId"
        fun createRoute(placeId: String): String = "location_details/$placeId"
    }

    data object QuickAction : Screen("quick_action/{category}", "Quick Action") {
        const val ARG_CATEGORY = "category"
        fun createRoute(category: String): String = "quick_action/$category"
    }

    data object ScheduleEditor : Screen("schedule_editor", "My Schedule")
}
