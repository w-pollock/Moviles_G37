package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AnalyticsEvent
import com.example.moviles_g37.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsDashboardScreen(
    dashboardViewModel: AnalyticsDashboardViewModel = viewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    val eventOrder = listOf(
        "screen_view",
        "search_submitted",
        "place_viewed",
        "route_started",
        "route_completed",
        "quick_action_clicked",
        "settings_sync_clicked",
        "flow_abandoned"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SenecaYellow)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Analytics Dashboard",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.recentEvents.size} events captured",
                        color = Color.Black.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Graphite)
                    .padding(16.dp)
            ) {
                Text(
                    text = "EVENT SUMMARY",
                    color = SenecaTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                eventOrder.forEach { eventName ->
                    val count = uiState.eventCounts[eventName] ?: 0
                    EventCountRow(eventName = eventName, count = count)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Onyx)
                    .padding(16.dp)
            ) {
                Text(
                    text = "BQ ANSWERS",
                    color = SenecaTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                BQAnswerCard(
                    bqId = "BQ4",
                    question = "Most visited places",
                    answer = "place_viewed x ${uiState.eventCounts["place_viewed"] ?: 0}",
                    owner = "Nicolás Godoy"
                )
                Spacer(modifier = Modifier.height(8.dp))
                BQAnswerCard(
                    bqId = "BQ6",
                    question = "Most used features",
                    answer = "screen_view x ${uiState.eventCounts["screen_view"] ?: 0} " +
                            "| quick_action x ${uiState.eventCounts["quick_action_clicked"] ?: 0}",
                    owner = "William Pollock"
                )
                Spacer(modifier = Modifier.height(8.dp))
                BQAnswerCard(
                    bqId = "BQ10",
                    question = "Abandoned flows",
                    answer = "flow_abandoned x ${uiState.eventCounts["flow_abandoned"] ?: 0}",
                    owner = "Daniel Reales"
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Graphite)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "RECENT EVENTS",
                    color = SenecaTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        val recentReversed = uiState.recentEvents.asReversed()
        items(recentReversed) { event ->
            EventRow(event = event)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EventCountRow(eventName: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(if (count > 0) SenecaYellow else SenecaTextSecondary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = eventName,
            color = SenecaTextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (count > 0) SenecaYellow else Onyx)
                .padding(horizontal = 10.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = if (count > 0) Color.Black else SenecaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BQAnswerCard(
    bqId: String,
    question: String,
    answer: String,
    owner: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Graphite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SenecaYellow)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bqId,
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = question, color = SenecaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = answer, color = SenecaYellow, fontSize = 12.sp)
            Text(text = owner, color = SenecaTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun EventRow(event: AnalyticsEvent) {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val time = formatter.format(Date(event.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Graphite)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.name,
                color = SenecaYellow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = time,
                color = SenecaTextSecondary,
                fontSize = 12.sp
            )
        }
        if (event.params.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.params.entries.joinToString(" | ") { "${it.key}: ${it.value}" },
                color = SenecaTextSecondary,
                fontSize = 12.sp
            )
        }
        HorizontalDivider(
            color = Onyx.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}