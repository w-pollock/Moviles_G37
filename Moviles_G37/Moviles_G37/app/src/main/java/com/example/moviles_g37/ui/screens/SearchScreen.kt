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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.*

@Composable
fun SearchScreen(
    onNavigateToLocationDetails: () -> Unit,
    searchViewModel: SearchViewModel = viewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
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
                text = "Results", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 14.dp)
            )
            HorizontalDivider(color = White.copy(alpha = 0.35f))
            uiState.results.forEach { result ->
                SearchResultRow(
                    icon = when (result.category) {
                        "study" -> Icons.Outlined.Book
                        "food" -> Icons.Outlined.LocalCafe
                        else -> Icons.Outlined.Place
                    },
                    text = result.name,
                    onClick = {
                        searchViewModel.onResultClicked(result)
                        onNavigateToLocationDetails()
                    }
                )
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
                uiState.recentSearches.forEachIndexed { index, search ->
                    Text(
                        text = search, color = SenecaTextPrimary, fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
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

@Composable
private fun SearchResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = White)
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = text, color = SenecaTextPrimary, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = "Open", tint = White)
        }
        HorizontalDivider(color = White.copy(alpha = 0.25f))
    }
}