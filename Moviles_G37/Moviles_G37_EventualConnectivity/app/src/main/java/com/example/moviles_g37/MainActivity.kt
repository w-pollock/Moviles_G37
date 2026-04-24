package com.example.moviles_g37

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.AppPreferences
import com.example.moviles_g37.data.PlacesRepository
import com.example.moviles_g37.navigation.AppNavigation
import com.example.moviles_g37.navigation.Screen
import com.example.moviles_g37.ui.components.BottomNavBar
import com.example.moviles_g37.ui.theme.Moviles_G37Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppContext.init(this)

        lifecycleScope.launch {
            launch(Dispatchers.IO) { PlacesRepository(applicationContext).seedIfEmpty() }
            AppPreferences.load()
        }

        // If Firebase restored a previous session, jump straight to Home.
        // Otherwise show the Auth screen first.
        val hasSession = AppAuth.currentState.hasSession
        val startRoute = if (hasSession) Screen.Home.route else Screen.Auth.route

        setContent {
            Moviles_G37Theme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                // Hide the bottom bar on the Auth screen so the sign-in flow
                // fills the whole viewport.
                val showBottomBar = currentRoute != null && currentRoute != Screen.Auth.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        startDestination = startRoute,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
