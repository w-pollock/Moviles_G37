package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.AppAuth
import com.example.moviles_g37.data.ScheduleEntry
import com.example.moviles_g37.data.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val nextClass: ScheduleEntry? = null,
    val isCurrentClassLive: Boolean = false,
    val hasSchedule: Boolean = false,
    val isGuest: Boolean = true,
    val isLoading: Boolean = true
)

class HomeViewModel : ViewModel() {

    private val scheduleRepository = ScheduleRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeAuth()
    }

    /** Called from the screen's LaunchedEffect to re-load when returning. */
    fun refresh() = loadSchedule()

    fun onNavigateToClassClicked() {
        val target = _uiState.value.nextClass ?: return
        AppAnalytics.track(
            event = "route_started",
            params = mapOf("origin_screen" to "home", "destination" to target.room)
        )
    }

    fun onQuickActionClicked(actionType: String) {
        AppAnalytics.track(
            event = "quick_action_clicked",
            params = mapOf("action_type" to actionType)
        )
    }

    fun onSearchBarClicked() {
        AppAnalytics.track(
            event = "quick_action_clicked",
            params = mapOf("action_type" to "search_bar")
        )
    }

    fun onSignInBannerClicked() {
        AppAnalytics.track("sign_in_prompt_clicked", mapOf("source" to "home_banner"))
    }

    private fun observeAuth() {
        viewModelScope.launch {
            AppAuth.authState.collect { state ->
                _uiState.update { it.copy(isGuest = state.isAnonymous) }
                // Reload schedule whenever auth changes — a real account may have data.
                loadSchedule()
            }
        }
    }

    private fun loadSchedule() {
        val isGuest = AppAuth.currentState.isAnonymous
        if (isGuest) {
            _uiState.update {
                it.copy(
                    nextClass = null, isCurrentClassLive = false,
                    hasSchedule = false, isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val schedule = scheduleRepository.getSchedule()
            val (next, isLive) = pickCurrentOrNext(schedule)
            _uiState.update {
                it.copy(
                    nextClass = next,
                    isCurrentClassLive = isLive,
                    hasSchedule = schedule.isNotEmpty(),
                    isLoading = false
                )
            }
        }
    }

    private fun pickCurrentOrNext(
        schedule: List<ScheduleEntry>
    ): Pair<ScheduleEntry?, Boolean> {
        if (schedule.isEmpty()) return null to false

        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_WEEK)
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        val live = schedule.firstOrNull { e ->
            e.dayOfWeek == today &&
                    nowMinutes in e.startTotalMinutes..e.endTotalMinutes
        }
        if (live != null) return live to true

        val next = schedule.minByOrNull { e -> minutesFromNow(today, nowMinutes, e) }
        return next to false
    }

    private fun minutesFromNow(today: Int, nowMinutes: Int, entry: ScheduleEntry): Int {
        val dayDiff = ((entry.dayOfWeek - today) + 7) % 7
        val minuteDiff = entry.startTotalMinutes - nowMinutes
        val raw = dayDiff * 24 * 60 + minuteDiff
        return if (raw < 0) raw + 7 * 24 * 60 else raw
    }
}
