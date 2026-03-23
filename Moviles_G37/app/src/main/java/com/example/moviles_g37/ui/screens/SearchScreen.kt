package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White

@Composable
fun SearchScreen(
    onNavigateToLocationDetails: () -> Unit
) {
    LaunchedEffect(Unit) {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "search")
        )
    }

    var searchText by remember { mutableStateOf("Mario Laserna") }

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
                onValueChange = {
                    searchText = it
                    AppAnalytics.track(
                        event = "search_submitted",
                        params = mapOf("query" to it)
                    )
                },
                placeholder = "Search building or room..."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
        ) {
            Text(
                text = "Results",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 14.dp)
            )

            HorizontalDivider(
                color = White.copy(alpha = 0.35f))

            SearchResultRow(
                icon = Icons.Outlined.Place,
                text = "ML Building",
                onClick = onNavigateToLocationDetails
            )

            SearchResultRow(
                icon = Icons.Outlined.Book,
                text = "ML Library",
                onClick = onNavigateToLocationDetails
            )

            SearchResultRow(
                icon = Icons.Outlined.LocalCafe,
                text = "Coffee Shop",
                onClick = onNavigateToLocationDetails
            )

            SearchResultRow(
                icon = Icons.Outlined.MeetingRoom,
                text = "Study Room",
                onClick = onNavigateToLocationDetails
            )
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SenecaYellow)
                .padding(16.dp)
        ) {
            Text(
                text = "Recent Searches",
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Graphite,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
            ) {
                RecentSearchItem("ML-603")
                RecentSearchDivider()
                RecentSearchItem("Chick n’ Chips")
                RecentSearchDivider()
                RecentSearchItem("SD-803")
                RecentSearchDivider()
                RecentSearchItem("O-202")
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                AppAnalytics.track(
                    event = "place_viewed",
                    params = mapOf(
                        "source_screen" to "search",
                        "place_name" to text
                    )
                )
                onClick()
            }
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = White
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = text,
                color = SenecaTextPrimary,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Open",
                tint = White
            )
        }

        HorizontalDivider(color = White.copy(alpha = 0.25f))
    }
}

@Composable
private fun RecentSearchItem(text: String) {
    Text(
        text = text,
        color = SenecaTextPrimary,
        fontSize = 17.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@Composable
private fun RecentSearchDivider() {
    HorizontalDivider(
        color = White.copy(alpha = 0.18f),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}