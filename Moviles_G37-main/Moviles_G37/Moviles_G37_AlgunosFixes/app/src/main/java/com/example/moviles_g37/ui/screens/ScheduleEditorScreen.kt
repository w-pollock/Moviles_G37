package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.ScheduleEntry
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.theme.*
import java.util.Calendar

@Composable
fun ScheduleEditorScreen(
    onBackClick: () -> Unit,
    viewModel: ScheduleEditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "schedule_editor"))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddNewClicked() },
                containerColor = SenecaYellow,
                contentColor = androidx.compose.ui.graphics.Color.Black
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add class")
            }
        },
        containerColor = Onyx
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Onyx)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SenecaYellow)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                SenecaHeader(title = "My Schedule", showBackButton = true, onBackClick = onBackClick)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add your weekly classes. They feed the Home widget and classroom calendars.",
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }

            Column(modifier = Modifier.fillMaxWidth().background(Graphite).padding(16.dp)) {
                when {
                    uiState.isLoading -> {
                        Text("Loading...", color = SenecaTextSecondary)
                    }
                    uiState.entries.isEmpty() -> {
                        Text(
                            "No classes yet. Tap + to add your first one.",
                            color = SenecaTextSecondary, fontSize = 14.sp
                        )
                    }
                    else -> {
                        uiState.entries.forEach { entry ->
                            ScheduleEntryCard(
                                entry = entry,
                                onEdit = { viewModel.onEditClicked(entry) },
                                onDelete = { viewModel.onDelete(entry.id) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (uiState.isDialogOpen && uiState.editingEntry != null) {
        ScheduleEntryDialog(
            initial = uiState.editingEntry!!,
            onDismiss = { viewModel.onDialogDismissed() },
            onSave = { viewModel.onSave(it) },
            onDelete = { viewModel.onDelete(uiState.editingEntry!!.id) }
        )
    }
}

@Composable
private fun ScheduleEntryCard(
    entry: ScheduleEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite.copy(alpha = 0.95f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SenecaYellow)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = entry.dayNameShort,
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.subject.ifBlank { "(untitled)" },
                color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${entry.timeString}  ·  ${entry.room}",
                color = SenecaTextSecondary, fontSize = 13.sp
            )
            if (entry.professor.isNotBlank()) {
                Text(text = entry.professor, color = SenecaTextSecondary, fontSize = 12.sp)
            }
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = White)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = White)
        }
    }
}

@Composable
private fun ScheduleEntryDialog(
    initial: ScheduleEntry,
    onDismiss: () -> Unit,
    onSave: (ScheduleEntry) -> Unit,
    onDelete: () -> Unit
) {
    var subject by remember { mutableStateOf(initial.subject) }
    var room by remember { mutableStateOf(initial.room) }
    var professor by remember { mutableStateOf(initial.professor) }
    var dayOfWeek by remember { mutableStateOf(initial.dayOfWeek) }
    var startHour by remember { mutableStateOf(initial.startHour.toString()) }
    var startMinute by remember { mutableStateOf(initial.startMinute.toString().padStart(2, '0')) }
    var endHour by remember { mutableStateOf(initial.endHour.toString()) }
    var endMinute by remember { mutableStateOf(initial.endMinute.toString().padStart(2, '0')) }

    val isNew = initial.id.isBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Graphite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isNew) "New class" else "Edit class",
                    color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                DialogTextField("Subject", subject) { subject = it }
                Spacer(modifier = Modifier.height(10.dp))
                DialogTextField("Room (e.g. ML-603)", room) { room = it }
                Spacer(modifier = Modifier.height(10.dp))
                DialogTextField("Professor", professor) { professor = it }
                Spacer(modifier = Modifier.height(14.dp))

                Text("Day", color = SenecaTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val days = listOf(
                        Calendar.MONDAY to "L", Calendar.TUESDAY to "M", Calendar.WEDNESDAY to "X",
                        Calendar.THURSDAY to "J", Calendar.FRIDAY to "V",
                        Calendar.SATURDAY to "S", Calendar.SUNDAY to "D"
                    )
                    days.forEach { (d, label) ->
                        val selected = dayOfWeek == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) SenecaYellow
                                    else DimGrey.copy(alpha = 0.3f)
                                )
                                .clickable { dayOfWeek = d }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) androidx.compose.ui.graphics.Color.Black else White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Start time", color = SenecaTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    DialogNumberField(
                        value = startHour, onValueChange = { startHour = it },
                        placeholder = "HH", modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(":", color = White, fontSize = 22.sp, modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    DialogNumberField(
                        value = startMinute, onValueChange = { startMinute = it },
                        placeholder = "MM", modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("End time", color = SenecaTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    DialogNumberField(
                        value = endHour, onValueChange = { endHour = it },
                        placeholder = "HH", modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(":", color = White, fontSize = 22.sp, modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    DialogNumberField(
                        value = endMinute, onValueChange = { endMinute = it },
                        placeholder = "MM", modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isNew) {
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
                        ) {
                            Text("Delete")
                        }
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = SenecaTextSecondary)
                    }
                    Button(
                        onClick = {
                            onSave(
                                initial.copy(
                                    subject = subject.trim(),
                                    room = room.trim(),
                                    professor = professor.trim(),
                                    dayOfWeek = dayOfWeek,
                                    startHour = startHour.toIntOrNull()?.coerceIn(0, 23) ?: 0,
                                    startMinute = startMinute.toIntOrNull()?.coerceIn(0, 59) ?: 0,
                                    endHour = endHour.toIntOrNull()?.coerceIn(0, 23) ?: 0,
                                    endMinute = endMinute.toIntOrNull()?.coerceIn(0, 59) ?: 0
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = subject.isNotBlank() && room.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SenecaYellow,
                            contentColor = androidx.compose.ui.graphics.Color.Black
                        )
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, color = SenecaTextSecondary) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = White, unfocusedTextColor = White,
            focusedBorderColor = SenecaYellow, unfocusedBorderColor = SenecaTextSecondary
        )
    )
}

@Composable
private fun DialogNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { txt -> onValueChange(txt.filter { it.isDigit() }.take(2)) },
        placeholder = { Text(placeholder, color = SenecaTextSecondary) },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = White, unfocusedTextColor = White,
            focusedBorderColor = SenecaYellow, unfocusedBorderColor = SenecaTextSecondary
        )
    )
}
