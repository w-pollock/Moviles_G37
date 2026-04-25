package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.theme.*

@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "analytics"))
        viewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SenecaYellow)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Analytics Dashboard",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = Color.Black)
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SenecaYellow)
            }
            return@Column
        }

        Spacer(Modifier.height(12.dp))

        // BQ 2: Top searched places
        BQCard(
            title = "BQ 2 · Lugares más buscados",
            icon = Icons.Outlined.Search,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (uiState.topSearchedPlaces.isEmpty()) {
                Text("Sin datos suficientes aún.", color = SenecaTextSecondary, fontSize = 13.sp)
            } else {
                uiState.topSearchedPlaces.forEachIndexed { i, (name, count) ->
                    BQBarRow(rank = i + 1, label = name, value = count, maxValue = uiState.topSearchedPlaces.first().second)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // BQ 9: Map vs Search
        BQCard(
            title = "BQ 9 · Mapa vs Búsqueda",
            icon = Icons.Outlined.CompareArrows,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BQStatBox(
                    label = "Vistas de Mapa",
                    value = uiState.mapViewCount.toString(),
                    icon = Icons.Outlined.Map,
                    modifier = Modifier.weight(1f)
                )
                BQStatBox(
                    label = "Búsquedas",
                    value = uiState.searchUseCount.toString(),
                    icon = Icons.Outlined.Search,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            val total = uiState.mapViewCount + uiState.searchUseCount
            if (total > 0) {
                val mapPct = (uiState.mapViewCount * 100f / total).toInt()
                Text(
                    text = "Mapa: $mapPct% · Búsqueda: ${100 - mapPct}%",
                    color = SenecaTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // BQ 10: Most favorited places
        BQCard(
            title = "BQ 10 · Lugares más guardados",
            icon = Icons.Outlined.Favorite,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (uiState.topFavoritedPlaces.isEmpty()) {
                Text("Sin favoritos registrados aún.", color = SenecaTextSecondary, fontSize = 13.sp)
            } else {
                uiState.topFavoritedPlaces.forEachIndexed { i, (name, count) ->
                    BQBarRow(rank = i + 1, label = name, value = count, maxValue = uiState.topFavoritedPlaces.first().second)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // BQ 11: Average session duration
        BQCard(
            title = "BQ 11 · Duración promedio de sesión",
            icon = Icons.Outlined.Timer,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            val ms = uiState.avgSessionDurationMs
            val formatted = when {
                ms == 0L -> "Sin datos aún"
                ms < 60_000 -> "${ms / 1000}s"
                else -> "${ms / 60_000}m ${(ms % 60_000) / 1000}s"
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = formatted, color = SenecaYellow, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Tiempo promedio por sesión", color = SenecaTextSecondary, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Recent Events
        BQCard(
            title = "Eventos recientes",
            icon = Icons.Outlined.History,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (uiState.recentEvents.isEmpty()) {
                Text("No events tracked yet.", color = SenecaTextSecondary, fontSize = 13.sp)
            } else {
                uiState.recentEvents.take(10).forEach { event ->
                    Text(
                        text = "• $event",
                        color = SenecaTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BQCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = SenecaYellow, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = title, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = White.copy(alpha = 0.15f))
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun BQBarRow(rank: Int, label: String, value: Long, maxValue: Long) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                color = SenecaYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp)
            )
            Text(
                text = label,
                color = SenecaTextPrimary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value.toString(),
                color = SenecaTextSecondary,
                fontSize = 12.sp
            )
        }
        if (maxValue > 0) {
            LinearProgressIndicator(
                progress = { (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().padding(start = 28.dp, top = 4.dp),
                color = SenecaYellow,
                trackColor = White.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun BQStatBox(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Onyx.copy(alpha = 0.6f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SenecaYellow, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(6.dp))
        Text(text = value, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = SenecaTextSecondary, fontSize = 11.sp)
    }
}
