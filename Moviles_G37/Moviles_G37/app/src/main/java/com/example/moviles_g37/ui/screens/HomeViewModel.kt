package com.example.moviles_g37.ui.screens

import android.provider.CalendarContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.ScheduleRepository
import com.example.moviles_g37.navigation.AppNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Async
import org.w3c.dom.Text
import java.util.Calendar
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
    private val scheduleRepository = ScheduleRepository()

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()



    init {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "home")
        )
        loadSchedule()
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

    private fun loadSchedule() {
        viewModelScope.launch {
            val schedule = scheduleRepository.getSchedule()
            if (schedule.isNotEmpty()){
                val currentOrNext = getCurrentOrNextClass(schedule)
                _uiState.update { it.copy(nextClass = currentOrNext ?: schedule.first()) }
            }
        }
    }

    private fun getCurrentOrNextClass(schedule: List<ScheduleItem>): ScheduleItem? {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        return schedule.firstOrNull{ item ->
            try {
                val times = item.time.split("-")
                if (times.size < 2) return@firstOrNull false

                val startParts = times[0].trim().split(":")
                val endParts = times[1].trim().split(":")

                val startTotal = startParts[0].toInt() * 60 + startParts[1].toInt()
                val endTotal = endParts[0].toInt() * 60 + endParts[1].toInt()

                currentTotalMinutes in startTotal..endTotal ||
                        currentTotalMinutes < startTotal
            } catch (e: Exception){
                false
            }
        }
    }

}