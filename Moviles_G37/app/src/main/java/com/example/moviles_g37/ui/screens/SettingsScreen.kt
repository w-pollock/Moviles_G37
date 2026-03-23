package com.example.moviles_g37.ui.screens

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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.theme.DimGrey
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaTextSecondary
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White


@Composable
fun SettingsScreen() {
    LaunchedEffect(Unit) {
        AppAnalytics.track(
            event = "screen_view",
            params = mapOf("screen_name" to "settings")
        )
    }

    var is3DBuildingsEnabled by remember { mutableStateOf(true) }
    var isIndoorNavigationEnabled by remember { mutableStateOf(true) }
    var isAccessibleRoutesEnabled by remember { mutableStateOf(false) }
    var selectedWalkingSpeed by remember { mutableStateOf("Normal") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsTopBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            SectionLabel("MAP OPTIONS")

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "3D Buildings",
                        subtitle = null,
                        checked = is3DBuildingsEnabled,
                        onCheckedChange = {
                            is3DBuildingsEnabled = it
                            AppAnalytics.track(
                                event = "setting_changed",
                                params = mapOf(
                                    "setting_name" to "3d_buildings",
                                    "value" to it
                                )
                            )
                        }
                    )

                    HorizontalDivider(color = White.copy(alpha = 0.12f))

                    SettingToggleRow(
                        title = "Indoor Navigation",
                        subtitle = null,
                        checked = isIndoorNavigationEnabled,
                        onCheckedChange = {
                            isIndoorNavigationEnabled = it
                            AppAnalytics.track(
                                event = "setting_changed",
                                params = mapOf(
                                    "setting_name" to "indoor_navigation",
                                    "value" to it
                                )
                            )
                        }
                    )

                    HorizontalDivider(color = White.copy(alpha = 0.12f))

                    SettingToggleRow(
                        title = "Accessible Routes",
                        subtitle = "Avoid stairs and elevators",
                        checked = isAccessibleRoutesEnabled,
                        onCheckedChange = {
                            isAccessibleRoutesEnabled = it
                            AppAnalytics.track(
                                event = "setting_changed",
                                params = mapOf(
                                    "setting_name" to "accessible_routes",
                                    "value" to it
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("SCHEDULE SYNC")

            Spacer(modifier = Modifier.height(12.dp))

            SyncCard(
                onSyncClick = {
                    AppAnalytics.track(
                        event = "settings_sync_clicked",
                        params = mapOf("provider" to "uniandes_account")
                    )
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("NAVIGATION PREFERENCES")

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Walking Speed",
                        color = SenecaTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WalkingSpeedSelector(
                        selectedSpeed = selectedWalkingSpeed,
                        onSpeedSelected = {
                            selectedWalkingSpeed = it
                            AppAnalytics.track(
                                event = "setting_changed",
                                params = mapOf(
                                    "setting_name" to "walking_speed",
                                    "value" to it
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = White.copy(alpha = 0.12f))

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Voice Alerts",
                            color = SenecaTextPrimary,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Spanish",
                            color = SenecaYellow,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                SettingsLinkRow("About SénecaMaps")
                HorizontalDivider(color = White.copy(alpha = 0.12f))
                SettingsLinkRow("Terms of Service")
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingsTopBar() {
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
                tint = androidx.compose.ui.graphics.Color.Black
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Settings",
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = SenecaTextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SenecaTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = SenecaTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SenecaYellow,
                checkedTrackColor = White.copy(alpha = 0.35f),
                uncheckedThumbColor = White,
                uncheckedTrackColor = White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun SyncCard(
    onSyncClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SenecaYellow)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Graphite.copy(alpha = 0.9f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "Account",
                tint = White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Uniandes Account",
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Connected",
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                fontSize = 13.sp
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Graphite)
                .clickable { onSyncClick() }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Sync,
                contentDescription = "Sync",
                tint = White
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Sync",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WalkingSpeedSelector(
    selectedSpeed: String,
    onSpeedSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DimGrey.copy(alpha = 0.28f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("Slow", "Normal", "Fast").forEach { speed ->
            val isSelected = speed == selectedSpeed

            Box(



                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) SenecaYellow else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSpeedSelected(speed) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = speed,
                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black else White,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SettingsLinkRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = SenecaTextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Open",
            tint = White
        )
    }
}