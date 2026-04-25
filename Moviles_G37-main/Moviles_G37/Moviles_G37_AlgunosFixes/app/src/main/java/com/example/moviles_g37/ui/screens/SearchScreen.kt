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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.*

@Composable
fun SearchScreen(
    onNavigateToLocationDetails: (String) -> Unit,
    searchViewModel: SearchViewModel = viewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "search"))
        searchViewModel.refreshRecents()
    }

    DisposableEffect(Unit) {
        onDispose { searchViewModel.onScreenAbandoned() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow).padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SenecaHeader(title = "SénecaMaps")
            Spacer(modifier = Modifier.height(18.dp))
            SenecaSearchBar(
                value = uiState.searchText,
                onValueChange = searchViewModel::onSearchTextChanged,
                placeholder = "Search building or room..."
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(modifier = Modifier.fillMaxWidth().background(Graphite)) {
            Text(
                text = "Results",
                color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 14.dp)
            )
            HorizontalDivider(color = White.copy(alpha = 0.35f))

            when {
                uiState.searchText.isBlank() -> {
                    Text(
                        text = "Start typing to search for buildings, rooms, food places, study spots and more.",
                        color = SenecaTextSecondary, fontSize = 14.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                uiState.results.isEmpty() -> {
                    Text(
                        text = "No places match \"${uiState.searchText}\".",
                        color = SenecaTextSecondary, fontSize = 14.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                else -> {
                    uiState.results.forEach { place ->
                        SearchResultRow(
                            icon = iconForCategory(place.category),
                            title = place.name,
                            subtitle = place.description.ifBlank { place.building },
                            onClick = {
                                searchViewModel.onResultClicked(place)
                                onNavigateToLocationDetails(place.id)
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow).padding(16.dp)
        ) {
            Text(
                text = "Recent Searches",
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Column(
                modifier = Modifier.fillMaxWidth().background(
                    color = Graphite, shape = RoundedCornerShape(20.dp)
                )
            ) {
                if (uiState.recentSearches.isEmpty()) {
                    Text(
                        text = "Your recent searches will appear here.",
                        color = SenecaTextSecondary, fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    uiState.recentSearches.forEachIndexed { index, search ->
                        Text(
                            text = search,
                            color = SenecaTextPrimary, fontSize = 17.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { searchViewModel.onRecentSearchClicked(search) }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                        if (index < uiState.recentSearches.lastIndex) {
                            HorizontalDivider(
                                color = White.copy(alpha = 0.18f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = White)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = SenecaTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotBlank()) {
                    Text(text = subtitle, color = SenecaTextSecondary, fontSize = 13.sp)
                }
            }
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = "Open", tint = White)
        }
        HorizontalDivider(color = White.copy(alpha = 0.25f))
    }
}

internal fun iconForCategory(category: String) = when (category.lowercase()) {
    "food" -> Icons.Outlined.Restaurant
    "study" -> Icons.Outlined.MenuBook
    "classroom" -> Icons.Outlined.School
    "restroom" -> Icons.Outlined.Wc
    "sports" -> Icons.Outlined.FitnessCenter
    "service" -> Icons.Outlined.Store
    else -> Icons.Outlined.Place
}
