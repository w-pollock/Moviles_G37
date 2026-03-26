package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.R
import com.example.moviles_g37.ui.theme.*

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val uiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().background(Onyx).verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow)
                .padding(horizontal = 12.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Favorites", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }



        Column(modifier = Modifier.fillMaxWidth().background(Graphite).padding(16.dp)) {
            TextField(
                value = uiState.searchText,
                onValueChange = favoritesViewModel::onSearchTextChanged,
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = SenecaTextSecondary)
                },
                placeholder = { Text("Search saved places", color = SenecaTextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White, unfocusedContainerColor = White,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Academic", "Food", "Study").forEach { filter ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (uiState.selectedFilter == filter) SenecaYellow else Graphite.copy(alpha = 0.85f))
                            .clickable { favoritesViewModel.onFilterSelected(filter) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = filter,
                            color = if (uiState.selectedFilter == filter) Color.Black else White,
                            fontSize = 13.sp,
                            fontWeight = if (uiState.selectedFilter == filter) FontWeight.Bold else FontWeight.Medium
                        )
                        if (filter == "Academic" || filter == "Food") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription = null,
                                tint = if (uiState.selectedFilter == filter) Color.Black else White)
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(18.dp))

            val filteredPlaces = if (uiState.selectedFilter == "All") uiState.places
            else uiState.places.filter { it.category == uiState.selectedFilter }

            filteredPlaces.forEach { place ->
                FavoritePlaceCard(
                    title = place.title,
                    subtitle = place.subtitle,
                    icon = when {
                        place.id == "biblioteca" -> Icons.Outlined.MenuBook
                        place.id == "ml" -> Icons.Outlined.Apartment
                        place.id == "cafeteria" -> Icons.Outlined.LocalCafe
                        else -> Icons.Outlined.FitnessCenter
                    },
                    onGoClick = { favoritesViewModel.onPlaceGoClicked(place) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ON THE MAP", color = SenecaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp))) {
                Image(
                    painter = painterResource(id = R.drawable.map_photo),
                    contentDescription = "Map preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier.align(Alignment.Center).clip(RoundedCornerShape(24.dp))
                        .background(Graphite.copy(alpha = 0.92f))
                        .clickable { favoritesViewModel.onNavigateToClassesClicked() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Map, contentDescription = null, tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Navigate to Classes", color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FavoritePlaceCard(title: String, subtitle: String, icon: ImageVector, onGoClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Graphite.copy(alpha = 0.95f)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Onyx.copy(alpha = 0.55f)).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = SenecaYellow)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SenecaTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = SenecaTextSecondary, fontSize = 13.sp)
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(SenecaYellow)
                .clickable { onGoClick() }.padding(horizontal = 16.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Go", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}