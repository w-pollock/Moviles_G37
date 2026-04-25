package com.example.moviles_g37.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moviles_g37.navigation.Screen
import com.example.moviles_g37.ui.theme.DimGrey
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.White

@Composable
fun BottomNavBar(navController: NavHostController) {
    val screens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Map,
        Screen.Favorites,
        Screen.Analytics,
        Screen.Settings
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(containerColor = Onyx) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Usar Screen.Home.route como raíz fija del back stack
                        // evita que findStartDestination() apunte a 'auth' y
                        // restaure el estado incorrecto al presionar Home.
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Home -> Icons.Outlined.Home
                            Screen.Search -> Icons.Outlined.Search
                            Screen.Map -> Icons.Outlined.Map
                            Screen.Favorites -> Icons.Outlined.StarOutline
                            Screen.Analytics -> Icons.Outlined.BarChart
                            Screen.Settings -> Icons.Outlined.Settings
                            else -> Icons.Outlined.Home
                        },
                        contentDescription = screen.title
                    )
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = White,
                    unselectedIconColor = White,
                    indicatorColor = DimGrey.copy(alpha = 0.35f)
                )
            )
        }
    }
}