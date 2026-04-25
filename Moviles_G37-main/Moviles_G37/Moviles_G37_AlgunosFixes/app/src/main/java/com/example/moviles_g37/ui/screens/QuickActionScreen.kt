package com.example.moviles_g37.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.theme.*

@Composable
fun QuickActionScreen(
    category: String,
    onBackClick: () -> Unit,
    onNavigateToLocationDetails: (String) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val vm: QuickActionViewModel = viewModel(
        key = "quick_$category",
        factory = QuickActionViewModel.Factory(application, category)
    )
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(category) {
        AppAnalytics.track(
            "screen_view",
            mapOf("screen_name" to "quick_action", "category" to category)
        )
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
            SenecaHeader(title = titleFor(category), showBackButton = true, onBackClick = onBackClick)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitleFor(category),
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                fontSize = 14.sp
            )
        }

        Column(modifier = Modifier.fillMaxWidth().background(Graphite).padding(vertical = 8.dp)) {
            when {
                uiState.isLoading -> {
                    Text(
                        text = "Loading...",
                        color = SenecaTextSecondary, fontSize = 14.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                uiState.places.isEmpty() -> {
                    Text(
                        text = "No places found in this category yet.",
                        color = SenecaTextSecondary, fontSize = 14.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                else -> {
                    uiState.places.forEach { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vm.onPlaceClicked(place)
                                    onNavigateToLocationDetails(place.id)
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
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
                                    imageVector = iconForCategory(place.category),
                                    contentDescription = place.name,
                                    tint = SenecaYellow
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.name,
                                    color = SenecaTextPrimary, fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                val sub = listOfNotNull(
                                    place.building.takeIf { it.isNotBlank() },
                                    place.floor?.let { "Piso $it" }
                                ).joinToString(" · ")
                                if (sub.isNotBlank()) {
                                    Text(text = sub, color = SenecaTextSecondary, fontSize = 13.sp)
                                }
                            }
                            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = "Open", tint = White)
                        }
                        HorizontalDivider(color = White.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

private fun titleFor(category: String) = when (category.lowercase()) {
    "food" -> "Food Spots"
    "study" -> "Study Spots"
    "restroom" -> "Restrooms"
    "classroom" -> "Classrooms"
    "sports" -> "Sports"
    else -> category.replaceFirstChar { it.uppercase() }
}

private fun subtitleFor(category: String) = when (category.lowercase()) {
    "food" -> "Cafeterías y comida rápida en el campus"
    "study" -> "Bibliotecas y salas de estudio"
    "restroom" -> "Baños cercanos"
    "classroom" -> "Salones disponibles"
    "sports" -> "Centros deportivos"
    else -> "Lugares relacionados"
}
