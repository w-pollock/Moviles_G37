package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.navigation.AppNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.Async
import org.w3c.dom.Text
import javax.security.auth.Subject

data class HomeUiState(
    val searchText: String = "",
    val nextClass: ScheduleItem = ScheduleItem(
        subject = "Constr. Aplicaciones Móviles",
        time = "9:30 - 11:00",
        room = "ML - 603",
        professor = "Mario Linares"
    )
)

data class ScheduleItem(
    val subject: String,
    val time: String,
    val room: String,
    val professor: String
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()



    init {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "home")
        )
    }
    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }


    fun onNavigateToClassClicked() {
        AppAnalytics.track(
            event = "route_started",
            params = mapOf(
                "origin_screen" to "home",
                "destination" to _uiState.value.nextClass.room
            )
        )
    }

    fun onQuickActionClicked(actionType: String) {
        AppAnalytics.track(

            event = "quick_action_clicked",
            params = mapOf("action_type" to actionType)
        )
    }
}