package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.data.ScheduleEntry
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.theme.*

@Composable
fun LocationDetailsScreen(
    placeId: String,
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: LocationDetailsViewModel = viewModel(
        key = "location_$placeId",
        factory = LocationDetailsViewModel.Factory(placeId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track(
            "screen_view",
            mapOf(
                "screen_name" to "location_details",
                "place_id" to placeId,
                "is_guest" to uiState.isGuest
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SenecaYellow)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SenecaHeader(
                        title = "SénecaMaps",
                        showBackButton = true,
                        onBackClick = onBackClick
                    )
                }
                // Favorites star only for authenticated users
                if (uiState.place != null && !uiState.isGuest) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (uiState.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = androidx.compose.ui.graphics.Color.Black,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { viewModel.toggleFavorite() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(White)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.place?.name ?: if (uiState.isLoading) "Loading..." else "Unknown place",
                    color = androidx.compose.ui.graphics.Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val subtitle = uiState.place?.let { p ->
                listOfNotNull(p.building.takeIf { it.isNotBlank() }, p.floor?.let { "Piso $it" })
                    .joinToString(" · ")
            }.orEmpty()
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            val pois = uiState.place?.nearbyPois.orEmpty()
            if (pois.isNotEmpty()) {
                Text(
                    text = "Puntos de interés cercanos",
                    color = SenecaTextSecondary,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                pois.forEach { poi ->
                    NearbyPoiItem(poi)
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val description = uiState.place?.description.orEmpty()
            if (description.isNotBlank() && pois.isEmpty()) {
                Text(text = description, color = SenecaTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Guest banner or current-class/calendar
            if (uiState.isGuest) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = White.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(14.dp))
                GuestDetailsBanner(
                    onSignInClick = {
                        viewModel.onSignInPromptClicked()
                        onNavigateToSettings()
                    }
                )
            } else {
                val current = uiState.currentClass
                Spacer(modifier = Modifier.height(8.dp))
                if (current != null) {
                    Text(
                        text = "Clase actual",
                        color = SenecaTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = current.subject,
                        color = SenecaTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        InfoLine(icon = Icons.Outlined.Schedule, text = current.timeString)
                        InfoLine(icon = Icons.Outlined.PersonOutline, text = current.professor)
                    }
                } else if (uiState.scheduleForThisRoom.isNotEmpty()) {
                    Text(
                        text = "No hay clase ahora en este salón.",
                        color = SenecaTextSecondary,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Agrega tus clases desde Settings → My Schedule para ver qué pasa aquí.",
                        color = SenecaTextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = White.copy(alpha = 0.45f))
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CALENDARIO SEMANAL",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                WeeklyCalendar(entries = uiState.weeklySchedule)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GuestDetailsBanner(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Onyx.copy(alpha = 0.55f))
            .clickable { onSignInClick() }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null, tint = SenecaYellow
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sign in for class info",
                color = SenecaTextPrimary,
                fontSize = 16.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The current class, weekly calendar and favorites for this place are available once you sign in with your Uniandes account.",
            color = SenecaTextSecondary, fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Go to Settings →",
            color = SenecaYellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun WeeklyCalendar(entries: List<ScheduleEntry>) {
    if (entries.isEmpty()) {
        Text(
            text = "Tu calendario aparecerá aquí cuando agregues tus clases en Settings.",
            color = SenecaTextSecondary,
            fontSize = 13.sp
        )
        return
    }

    val byDay = entries.groupBy { it.dayOfWeek }
    val sortedDays = byDay.keys.sortedWith(
        compareBy { if (it == java.util.Calendar.SUNDAY) 8 else it }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Onyx.copy(alpha = 0.55f))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        sortedDays.forEach { day ->
            val dayEntries = byDay[day].orEmpty().sortedBy { it.startTotalMinutes }
            val dayName = dayEntries.first().dayName
            Text(
                text = dayName.uppercase(),
                color = SenecaYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            dayEntries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.timeString,
                        color = SenecaTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.width(110.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.subject,
                            color = SenecaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${entry.room}  ·  ${entry.professor}",
                            color = SenecaTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            HorizontalDivider(color = White.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun NearbyPoiItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = White)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = SenecaTextPrimary, fontSize = 16.sp)
    }
}

@Composable
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = SenecaTextPrimary, fontSize = 16.sp)
    }
}
