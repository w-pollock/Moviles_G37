package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.theme.*

@Composable
fun SettingsScreen(
    onEditSchedule: () -> Unit,
    onSignedOut: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppAnalytics.track(
            "screen_view",
            mapOf("screen_name" to "settings", "is_guest" to uiState.isGuest)
        )
    }

    // When the VM reports a sign-out, hop back to the Auth screen.
    LaunchedEffect(uiState.signedOut) {
        if (uiState.signedOut) {
            onSignedOut()
            settingsViewModel.consumeSignedOut()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Onyx).verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow)
                .padding(horizontal = 12.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Settings", color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 24.sp, fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.fillMaxWidth().background(Graphite).padding(16.dp)) {

            // --- Account card (switches UI based on guest status) --------------
            Text(
                text = "ACCOUNT", color = SenecaTextSecondary,
                fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isGuest) {
                GuestAccountCard(
                    onSignInClick = { settingsViewModel.onSignInRequested() }
                )
            } else {
                AuthenticatedAccountCard(
                    userEmail = uiState.currentUser?.email ?: "(no email)",
                    isSyncing = uiState.isSyncing,
                    syncSuccess = uiState.syncSuccess,
                    onSyncClick = { settingsViewModel.onSyncClicked() },
                    onSignOutClick = { settingsViewModel.onSignOutRequested() }
                )
            }

            // --- Map options --------------------------------------------------
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "MAP OPTIONS", color = SenecaTextSecondary,
                fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        "3D Buildings", null,
                        uiState.preferences.is3DBuildingsEnabled,
                        settingsViewModel::on3DBuildingsToggled
                    )
                    HorizontalDivider(color = White.copy(alpha = 0.12f))
                    SettingToggleRow(
                        "Indoor Navigation", null,
                        uiState.preferences.isIndoorNavigationEnabled,
                        settingsViewModel::onIndoorNavigationToggled
                    )
                    HorizontalDivider(color = White.copy(alpha = 0.12f))
                    SettingToggleRow(
                        "Accessible Routes", "Avoid stairs and elevators",
                        uiState.preferences.isAccessibleRoutesEnabled,
                        settingsViewModel::onAccessibleRoutesToggled
                    )
                }
            }

            // --- My Schedule (only for signed-in users) ----------------------
            if (!uiState.isGuest) {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "MY SCHEDULE", color = SenecaTextSecondary,
                    fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onEditSchedule() }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null, tint = SenecaYellow
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit weekly schedule",
                                color = SenecaTextPrimary, fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Used for Next Class and classroom calendars",
                                color = SenecaTextSecondary, fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = null, tint = White
                        )
                    }
                }
            }

            // --- Navigation preferences --------------------------------------
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "NAVIGATION PREFERENCES", color = SenecaTextSecondary,
                fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Walking Speed",
                        color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(DimGrey.copy(alpha = 0.28f)).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Slow", "Normal", "Fast").forEach { speed ->
                            val isSelected = speed == uiState.preferences.walkingSpeed
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) SenecaYellow
                                        else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .clickable { settingsViewModel.onWalkingSpeedSelected(speed) }
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
            }

            // --- About / terms -----------------------------------------------
            Spacer(modifier = Modifier.height(22.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "About SénecaMaps", color = SenecaTextPrimary,
                        fontSize = 16.sp, modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = null, tint = White
                    )
                }
                HorizontalDivider(color = White.copy(alpha = 0.12f))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terms of Service", color = SenecaTextPrimary,
                        fontSize = 16.sp, modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = null, tint = White
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun GuestAccountCard(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Graphite.copy(alpha = 0.95f))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(Onyx.copy(alpha = 0.7f)).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null, tint = SenecaTextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Guest mode",
                    color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sign in to unlock schedule, favorites and the classroom calendar.",
                    color = SenecaTextSecondary, fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onSignInClick,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SenecaYellow,
                contentColor = androidx.compose.ui.graphics.Color.Black
            )
        ) {
            Text("Sign in to my account", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AuthenticatedAccountCard(
    userEmail: String,
    isSyncing: Boolean,
    syncSuccess: Boolean,
    onSyncClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(SenecaYellow).padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(Graphite.copy(alpha = 0.9f)).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Outlined.PersonOutline, contentDescription = null, tint = White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userEmail, color = androidx.compose.ui.graphics.Color.Black,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (syncSuccess) "Synced ✓" else "Signed in",
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Sync
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Graphite)
                    .clickable(enabled = !isSyncing) { onSyncClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), color = White, strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Icons.Outlined.Sync, contentDescription = null, tint = White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSyncing) "Syncing..." else "Sync",
                    color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
            }
            // Sign out
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Onyx.copy(alpha = 0.85f))
                    .clickable { onSignOutClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sign out",
                    color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String, subtitle: String?,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) Text(text = subtitle, color = SenecaTextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SenecaYellow,
                checkedTrackColor = White.copy(alpha = 0.35f),
                uncheckedThumbColor = White,
                uncheckedTrackColor = White.copy(alpha = 0.2f)
            )
        )
    }
}
