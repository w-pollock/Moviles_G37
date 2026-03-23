package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles_g37.R
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaTextSecondary
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White


@Composable
fun FavoritesScreen() {
    LaunchedEffect(Unit) {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "favorites")
        )
    }

    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        FavoritesTopBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            FavoritesSearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = "Search saved places"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "All",
                    selected = true,
                    showDropdown = false
                )

                FilterChip(
                    text = "Academic",
                    selected = false,
                    showDropdown = true
                )

                FilterChip(
                    text = "Food",
                    selected = false,
                    showDropdown = true
                )

                FilterChip(
                    text = "Study",
                    selected = false,
                    showDropdown = false
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FavoritePlaceCard(
                title = "Biblioteca General",
                subtitle = "Study & Research Center",
                icon = Icons.Outlined.MenuBook
            )

            Spacer(modifier = Modifier.height(12.dp))

            FavoritePlaceCard(
                title = "Edificio Mario Laserna",
                subtitle = "Engineering Faculty",
                icon = Icons.Outlined.Apartment
            )

            Spacer(modifier = Modifier.height(12.dp))

            FavoritePlaceCard(
                title = "Cafetería Central",
                subtitle = "Dining & Social",
                icon = Icons.Outlined.LocalCafe
            )

            Spacer(modifier = Modifier.height(12.dp))

            FavoritePlaceCard(
                title = "Centro Deportivo",
                subtitle = "Sports & Recreation",
                icon = Icons.Outlined.FitnessCenter
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ON THE MAP",
                color = SenecaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.map_photo),
                    contentDescription = "Map preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Graphite.copy(alpha = 0.92f))
                        .clickable {
                            AppAnalytics.track(
                                event = "route_started",
                                params = mapOf(
                                    "origin_screen" to "favorites",
                                    "destination" to "classes"
                                )
                            )
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = null,
                            tint = White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Navigate to Classes",
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FavoritesTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SenecaYellow)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Favorites",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FavoritesSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = SenecaTextSecondary
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = SenecaTextSecondary,
                fontSize = 14.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            disabledContainerColor = White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            cursorColor = Color.Black
        )
    )
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    showDropdown: Boolean
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) SenecaYellow else Graphite.copy(alpha = 0.85f))
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else White,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )

        if (showDropdown) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = if (selected) Color.Black else White
            )
        }
    }
}

@Composable
private fun FavoritePlaceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
                .clip(RoundedCornerShape(12.dp))
                .background(Onyx.copy(alpha = 0.55f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SenecaYellow
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SenecaTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = SenecaTextSecondary,
                fontSize = 13.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SenecaYellow)
                .clickable {
                    AppAnalytics.track(
                        event = "favorite_opened",
                        params = mapOf("place_name" to title)
                    )
                }
                .padding(horizontal = 16.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Go",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}