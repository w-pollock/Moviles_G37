package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.example.moviles_g37.ui.components.*
import com.example.moviles_g37.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToLocationDetails: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToQuickAction: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track(
            "screen_view",
            mapOf("screen_name" to "home", "is_guest" to uiState.isGuest)
        )
        homeViewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        OfflineBanner(
            offlineMessage = "Offline — schedule and favorites from your last session"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SenecaYellow)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SenecaHeader(title = "SénecaMaps")
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clickable {
                    homeViewModel.onSearchBarClicked()
                    onNavigateToSearch()
                }
            ) {
                SenecaSearchBar(
                    value = "",
                    onValueChange = {},
                    placeholder = "Search building or room...",
                    readOnly = true
                )
            }
        }

        // Schedule section: real card for authenticated users, sign-in prompt for guests
        if (uiState.isGuest) {
            GuestScheduleBanner(
                onSignInClick = {
                    homeViewModel.onSignInBannerClicked()
                    onNavigateToSettings()
                }
            )
        } else {
            ScheduleCard(
                uiState = uiState,
                onNavigateToClass = {
                    homeViewModel.onNavigateToClassClicked()
                    uiState.nextClass?.let {
                        onNavigateToLocationDetails(roomToPlaceId(it.room))
                    }
                },
                onEditSchedule = {
                    homeViewModel.onSignInBannerClicked()
                    onNavigateToSettings()
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SenecaYellow)
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Map", icon = Icons.Outlined.Map,
                    onClick = {
                        homeViewModel.onQuickActionClicked("map")
                        onNavigateToMap()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Food", icon = Icons.Outlined.Restaurant,
                    onClick = {
                        homeViewModel.onQuickActionClicked("food")
                        onNavigateToQuickAction("food")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Study", icon = Icons.Outlined.Book,
                    onClick = {
                        homeViewModel.onQuickActionClicked("study")
                        onNavigateToQuickAction("study")
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Restrooms", icon = Icons.Outlined.Wc,
                    onClick = {
                        homeViewModel.onQuickActionClicked("restroom")
                        onNavigateToQuickAction("restroom")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GuestScheduleBanner(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Graphite)
            .padding(16.dp)
    ) {
        Text(
            text = "Today's Schedule",
            color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Onyx.copy(alpha = 0.55f))
                .clickable { onSignInClick() }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = SenecaYellow
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign in to see your schedule",
                    color = SenecaTextPrimary,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Guests can explore the campus map and search places. Sign in to unlock your classes, favorites and the classroom calendar.",
                color = SenecaTextSecondary, fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Go to Settings →",
                color = SenecaYellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ScheduleCard(
    uiState: HomeUiState,
    onNavigateToClass: () -> Unit,
    onEditSchedule: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Graphite)
            .padding(16.dp)
    ) {
        Text(
            text = "Today's Schedule",
            color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        SectionSubtitle(
            when {
                uiState.isCurrentClassLive -> "In class now"
                uiState.nextClass != null -> "Next Class"
                else -> "No classes scheduled"
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = White.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        val next = uiState.nextClass
        if (next != null) {
            Text(
                text = next.subject,
                color = SenecaTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            ScheduleInfoLine(
                icon = Icons.Outlined.Schedule,
                text = "${next.dayName} · ${next.timeString}"
            )
            Spacer(modifier = Modifier.height(10.dp))
            ScheduleInfoLine(icon = Icons.Outlined.Place, text = next.room)
            Spacer(modifier = Modifier.height(10.dp))
            ScheduleInfoLine(icon = Icons.Outlined.PersonOutline, text = next.professor)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryActionButton(
                text = "Navigate to Class",
                icon = Icons.Outlined.Navigation,
                onClick = onNavigateToClass
            )
        } else {
            Text(
                text = "You haven't added any classes yet.",
                color = SenecaTextSecondary, fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap below to add your weekly classes.",
                color = SenecaTextSecondary, fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryActionButton(
                text = "Add classes",
                icon = Icons.Outlined.CalendarMonth,
                onClick = onEditSchedule
            )
        }
    }
}

internal fun roomToPlaceId(room: String): String =
    room.trim().lowercase()
        .replace(" ", "-")
        .replace(Regex("[^a-z0-9-]"), "")
        .ifEmpty { "ml-building" }
