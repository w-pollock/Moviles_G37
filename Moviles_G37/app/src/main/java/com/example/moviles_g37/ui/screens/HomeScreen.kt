package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.components.PrimaryActionButton
import com.example.moviles_g37.ui.components.QuickActionCard
import com.example.moviles_g37.ui.components.ScheduleInfoLine
import com.example.moviles_g37.ui.components.SectionSubtitle
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White


@Composable
fun HomeScreen(
    onNavigateToLocationDetails: () -> Unit
) {
    LaunchedEffect(Unit) {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "home")
        )
    }

    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

            SenecaHeader(title = "SénecaMaps")

            Spacer(modifier = Modifier.height(18.dp))

            SenecaSearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Search building or room..."
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            Text(
                text = "Today’s Schedule",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            SectionSubtitle("Next Class")

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Constr. Aplicaciones Móviles",
                color = SenecaTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            ScheduleInfoLine(
                icon = Icons.Outlined.Schedule,
                text = "9:30 - 11:00"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ScheduleInfoLine(
                icon = Icons.Outlined.Place,
                text = "ML-603"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ScheduleInfoLine(
                icon = Icons.Outlined.PersonOutline,
                text = "Mario Linares"
            )

            Spacer(modifier = Modifier.height(18.dp))

            PrimaryActionButton(
                text = "Navigate to Class",
                icon = Icons.Outlined.Navigation,
                onClick = {
                    AppAnalytics.track(
                        event = "route_started",
                        params = mapOf(
                            "origin_screen" to "home",
                            "destination" to "ML-603"
                        )
                    )
                    onNavigateToLocationDetails()
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Map",
                    icon = Icons.Outlined.Map,
                    onClick = {
                        AppAnalytics.track(
                            event = "quick_action_clicked",
                            params = mapOf("action_type" to "map")
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Food",
                    icon = Icons.Outlined.Restaurant,
                    onClick = {
                        AppAnalytics.track(
                            event = "quick_action_clicked",
                            params = mapOf("action_type" to "food")
                        )
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
                    title = "Study",
                    icon = Icons.Outlined.Book,
                    onClick = {
                        AppAnalytics.track(
                            event = "quick_action_clicked",
                            params = mapOf("action_type" to "study")
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Restrooms",
                    icon = Icons.Outlined.Wc,
                    onClick = {
                        AppAnalytics.track(
                            event = "quick_action_clicked",
                            params = mapOf("action_type" to "restrooms")
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}