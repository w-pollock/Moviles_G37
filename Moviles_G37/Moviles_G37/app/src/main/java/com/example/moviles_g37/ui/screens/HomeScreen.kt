package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.ui.components.*
import com.example.moviles_g37.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToLocationDetails: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()



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
                value = uiState.searchText,
                onValueChange = homeViewModel::onSearchTextChanged,
                placeholder = "Search building or room..."
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            Text(text = "Today's Schedule", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            SectionSubtitle("Next Class")
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = White.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = uiState.nextClass.subject, color = SenecaTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            ScheduleInfoLine(icon = Icons.Outlined.Schedule, text = uiState.nextClass.time)
            Spacer(modifier = Modifier.height(10.dp))
            ScheduleInfoLine(icon = Icons.Outlined.Place, text = uiState.nextClass.room)
            Spacer(modifier = Modifier.height(10.dp))
            ScheduleInfoLine(icon = Icons.Outlined.PersonOutline, text = uiState.nextClass.professor)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryActionButton(
                text = "Navigate to Class",
                icon = Icons.Outlined.Navigation,
                onClick = {
                    homeViewModel.onNavigateToClassClicked()
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
                QuickActionCard(title = "Map", icon = Icons.Outlined.Map,
                    onClick = { homeViewModel.onQuickActionClicked("map") }, modifier = Modifier.weight(1f))
                QuickActionCard(title = "Food", icon = Icons.Outlined.Restaurant,
                    onClick = { homeViewModel.onQuickActionClicked("food") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(title = "Study", icon = Icons.Outlined.Book,
                    onClick = { homeViewModel.onQuickActionClicked("study") }, modifier = Modifier.weight(1f))
                QuickActionCard(title = "Restrooms", icon = Icons.Outlined.Wc,
                    onClick = { homeViewModel.onQuickActionClicked("restrooms") }, modifier = Modifier.weight(1f))
            }
        }
    }
}

