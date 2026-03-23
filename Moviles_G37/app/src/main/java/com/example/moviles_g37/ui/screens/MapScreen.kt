package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles_g37.R
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.components.SenecaSearchBar
import com.example.moviles_g37.ui.theme.DimGrey
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White

@Composable
fun MapScreen(
    onNavigateToLocationDetails: () -> Unit
) {
    LaunchedEffect(Unit) {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "map")
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

            Spacer(modifier = Modifier.height(14.dp))

            Image(
                painter = painterResource(id = R.drawable.map_photo),
                contentDescription = "Campus map",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        AppAnalytics.track(
                            event = "place_viewed",
                            params = mapOf(
                                "source_screen" to "map",
                                "place_name" to "ML-603"
                            )
                        )
                        onNavigateToLocationDetails()
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.building_photo),
                contentDescription = "Building photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        AppAnalytics.track(
                            event = "place_viewed",
                            params = mapOf(
                                "source_screen" to "map",
                                "place_name" to "ML-603"
                            )
                        )
                        onNavigateToLocationDetails()
                    }
            )

            Spacer(modifier = Modifier.height(14.dp))

            LocationSummaryCard(
                title = "Edificio ML - 603",
                subtitle = "Sala de Clases – Piso 6",
                onClick = {
                    AppAnalytics.track(
                        event = "route_started",
                        params = mapOf(
                            "origin_screen" to "map",
                            "destination" to "ML-603"
                        )
                    )
                    onNavigateToLocationDetails()
                }
            )
        }
    }
}

@Composable
private fun LocationSummaryCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Graphite)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitle,
            color = White.copy(alpha = 0.75f),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DimGrey,
                contentColor = White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Navigation,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Navigate to Class",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}