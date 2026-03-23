package com.example.moviles_g37.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviles_g37.ui.screens.FavoritesScreen
import com.example.moviles_g37.ui.screens.HomeScreen
import com.example.moviles_g37.ui.screens.LocationDetailsScreen
import com.example.moviles_g37.ui.screens.MapScreen
import com.example.moviles_g37.ui.screens.SearchScreen
import com.example.moviles_g37.ui.screens.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLocationDetails = {
                    navController.navigate(Screen.LocationDetails.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToLocationDetails = {
                    navController.navigate(Screen.LocationDetails.route)
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToLocationDetails = {
                    navController.navigate(Screen.LocationDetails.route)
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }

        composable(Screen.LocationDetails.route) {
            LocationDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}