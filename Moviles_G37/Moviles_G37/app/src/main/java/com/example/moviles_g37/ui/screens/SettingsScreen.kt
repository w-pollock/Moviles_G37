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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.ui.theme.*

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var showLoginDialog by remember { mutableStateOf(false) }
    if (showLoginDialog) {
        LoginDialog(
            onDismiss = { showLoginDialog = false; settingsViewModel.clearAuthError() },
            onSignIn = { email, password -> settingsViewModel.signInWithEmail(email, password); showLoginDialog = false },
            onSignInAnonymously = { settingsViewModel.signInAnonymously(); showLoginDialog = false },
            isLoading = uiState.isAuthLoading,
            errorMessage = uiState.authError
        )
    }


    Column(modifier = Modifier.fillMaxSize().background(Onyx).verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().background(SenecaYellow)
                .padding(horizontal = 12.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back",
                    tint = androidx.compose.ui.graphics.Color.Black)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Settings", color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxWidth().background(Graphite).padding(16.dp)) {
            Text(text = "MAP OPTIONS", color = SenecaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow("3D Buildings", null, uiState.is3DBuildingsEnabled, settingsViewModel::on3DBuildingsToggled)
                    HorizontalDivider(color = White.copy(alpha = 0.12f))
                    SettingToggleRow("Indoor Navigation", null, uiState.isIndoorNavigationEnabled, settingsViewModel::onIndoorNavigationToggled)
                    HorizontalDivider(color = White.copy(alpha = 0.12f))
                    SettingToggleRow("Accessible Routes", "Avoid stairs and elevators", uiState.isAccessibleRoutesEnabled, settingsViewModel::onAccessibleRoutesToggled)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(text = "SCHEDULE SYNC", color = SenecaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.currentUser != null) {
                AuthenticatedSyncCard(
                    userEmail = uiState.currentUser?.email ?: "Anonymous User",
                    isSyncing = uiState.isSyncing,
                    syncSuccess = uiState.syncSuccess,
                    onSyncClick = settingsViewModel::onSyncClicked,
                    onSignOutClick = settingsViewModel::signOut
                )
            } else {
                UnauthenticatedCard(isLoading = uiState.isAuthLoading, onSignInClick = { showLoginDialog = true })
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(text = "NAVIGATION PREFERENCES", color = SenecaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Walking Speed", color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(DimGrey.copy(alpha = 0.28f)).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Slow", "Normal", "Fast").forEach { speed ->
                            val isSelected = speed == uiState.selectedWalkingSpeed
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SenecaYellow else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable { settingsViewModel.onWalkingSpeedSelected(speed) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = speed,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black else White,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Voice Alerts", color = SenecaTextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        Text(text = "Spanish", color = SenecaYellow, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.95f))) {
                Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "About SénecaMaps", color = SenecaTextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = White)
                }
                HorizontalDivider(color = White.copy(alpha = 0.12f))
                Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Terms of Service", color = SenecaTextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = White)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingToggleRow(title: String, subtitle: String?, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) Text(text = subtitle, color = SenecaTextSecondary, fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SenecaYellow, checkedTrackColor = White.copy(alpha = 0.35f),
                uncheckedThumbColor = White, uncheckedTrackColor = White.copy(alpha = 0.2f)))
    }
}

@Composable
private fun AuthenticatedSyncCard(userEmail: String, isSyncing: Boolean, syncSuccess: Boolean, onSyncClick: () -> Unit, onSignOutClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SenecaYellow).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Graphite.copy(alpha = 0.9f)).padding(10.dp),
            contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Outlined.PersonOutline, contentDescription = null, tint = White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userEmail, color = androidx.compose.ui.graphics.Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = if (syncSuccess) "Synced ✓" else "Connected",
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f), fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Graphite)
                .clickable { if (!isSyncing) onSyncClick() }.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (isSyncing) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = White, strokeWidth = 2.dp)
                else Icon(imageVector = Icons.Outlined.Sync, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isSyncing) "Syncing..." else "Sync", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Sign out", color = Graphite, fontSize = 12.sp, modifier = Modifier.clickable { onSignOutClick() })
        }
    }
}

@Composable
private fun UnauthenticatedCard(isLoading: Boolean, onSignInClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Graphite.copy(alpha = 0.95f)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Onyx.copy(alpha = 0.7f)).padding(10.dp),
            contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Outlined.PersonOutline, contentDescription = null, tint = SenecaTextSecondary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Uniandes Account", color = SenecaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Not connected", color = SenecaTextSecondary, fontSize = 13.sp)
        }
        Button(onClick = { if (!isLoading) onSignInClick() }, shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SenecaYellow, contentColor = Onyx)) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Onyx, strokeWidth = 2.dp)
            else Text(text = "Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun LoginDialog(onDismiss: () -> Unit, onSignIn: (String, String) -> Unit,
                        onSignInAnonymously: () -> Unit, isLoading: Boolean, errorMessage: String?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Graphite)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Uniandes Account", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Sign in to sync your schedule", color = SenecaTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = email, onValueChange = { email = it },
                    label = { Text("Email", color = SenecaTextSecondary) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White,
                        focusedBorderColor = SenecaYellow, unfocusedBorderColor = SenecaTextSecondary))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text("Password", color = SenecaTextSecondary) }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = White, unfocusedTextColor = White,
                        focusedBorderColor = SenecaYellow, unfocusedBorderColor = SenecaTextSecondary))
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = androidx.compose.ui.graphics.Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { onSignIn(email, password) },
                    enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SenecaYellow, contentColor = Onyx)) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Onyx, strokeWidth = 2.dp)
                    else Text("Sign In", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onSignInAnonymously) {
                    Text("Continue as Guest", color = SenecaTextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}