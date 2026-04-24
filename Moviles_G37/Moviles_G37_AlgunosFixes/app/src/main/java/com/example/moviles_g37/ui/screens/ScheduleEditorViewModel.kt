package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.ScheduleEntry
import com.example.moviles_g37.data.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScheduleEditorUiState(
    val entries: List<ScheduleEntry> = emptyList(),
    val isLoading: Boolean = true,
    val editingEntry: ScheduleEntry? = null,
    val isDialogOpen: Boolean = false
)

class ScheduleEditorViewModel : ViewModel() {

    private val repository = ScheduleRepository()

    private val _uiState = MutableStateFlow(ScheduleEditorUiState())
    val uiState: StateFlow<ScheduleEditorUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val entries = repository.getSchedule()
            _uiState.update { it.copy(entries = entries, isLoading = false) }
        }
    }

    fun onAddNewClicked() {
        _uiState.update {
            it.copy(isDialogOpen = true, editingEntry = ScheduleEntry())
        }
    }

    fun onEditClicked(entry: ScheduleEntry) {
        _uiState.update { it.copy(isDialogOpen = true, editingEntry = entry) }
    }

    fun onDialogDismissed() {
        _uiState.update { it.copy(isDialogOpen = false, editingEntry = null) }
    }

    fun onSave(entry: ScheduleEntry) {
        viewModelScope.launch {
            repository.addOrUpdate(entry)
            _uiState.update { it.copy(isDialogOpen = false, editingEntry = null) }
            AppAnalytics.track(
                "schedule_entry_saved",
                mapOf("subject" to entry.subject, "room" to entry.room)
            )
            refresh()
        }
    }

    fun onDelete(entryId: String) {
        if (entryId.isBlank()) return
        viewModelScope.launch {
            repository.delete(entryId)
            _uiState.update { it.copy(isDialogOpen = false, editingEntry = null) }
            AppAnalytics.track("schedule_entry_deleted", mapOf("entry_id" to entryId))
            refresh()
        }
    }
}
