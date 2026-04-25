package com.example.moviles_g37.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moviles_g37.ui.screens.AnalyticsDashboardScreen
import com.example.moviles_g37.ui.screens.AuthScreen
import com.example.moviles_g37.ui.screens.FavoritesScreen
import com.example.moviles_g37.ui.screens.HomeScreen
import com.example.moviles_g37.ui.screens.LocationDetailsScreen
import com.example.moviles_g37.ui.screens.MapScreen
import com.example.moviles_g37.ui.screens.QuickActionScreen
import com.example.moviles_g37.ui.screens.ScheduleEditorScreen
import com.example.moviles_g37.ui.screens.SearchScreen
import com.example.moviles_g37.ui.screens.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthenticated = {

                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToLocationDetails = { placeId ->
                    navController.navigate(Screen.LocationDetails.createRoute(placeId))
                },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToQuickAction = { category ->
                    navController.navigate(Screen.QuickAction.createRoute(category))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToLocationDetails = { placeId ->
                    navController.navigate(Screen.LocationDetails.createRoute(placeId))
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToLocationDetails = { placeId ->
                    navController.navigate(Screen.LocationDetails.createRoute(placeId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateToLocationDetails = { placeId ->
                    navController.navigate(Screen.LocationDetails.createRoute(placeId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsDashboardScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onEditSchedule = { navController.navigate(Screen.ScheduleEditor.route) },
                onSignedOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.ScheduleEditor.route) {
            ScheduleEditorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LocationDetails.route,
            arguments = listOf(
                navArgument(Screen.LocationDetails.ARG_PLACE_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments
                ?.getString(Screen.LocationDetails.ARG_PLACE_ID).orEmpty()
            LocationDetailsScreen(
                placeId = placeId,
                onBackClick = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.QuickAction.route,
            arguments = listOf(
                navArgument(Screen.QuickAction.ARG_CATEGORY) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments
                ?.getString(Screen.QuickAction.ARG_CATEGORY).orEmpty()
            QuickActionScreen(
                category = category,
                onBackClick = { navController.popBackStack() },
                onNavigateToLocationDetails = { placeId ->
                    navController.navigate(Screen.LocationDetails.createRoute(placeId))
                }
            )
        }
    }
}
