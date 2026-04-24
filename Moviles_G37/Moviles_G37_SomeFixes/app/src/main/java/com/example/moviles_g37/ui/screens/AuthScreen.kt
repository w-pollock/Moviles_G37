package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles_g37.R
import com.example.moviles_g37.analytics.AppAnalytics
import com.example.moviles_g37.ui.theme.*

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "auth"))
    }

    // When authentication succeeds, signal the NavHost to move on.
    LaunchedEffect(uiState.authSuccess) {
        if (uiState.authSuccess) {
            onAuthenticated()
            viewModel.consumeAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Onyx)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.seneca_cabra),
                contentDescription = "SénecaMaps logo",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SénecaMaps",
                color = SenecaYellow,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your campus companion",
                color = SenecaTextSecondary,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Sign-in card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Graphite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Sign in to your account",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Access your schedule, favorites and personalised features.",
                    color = SenecaTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (uiState.errorMessage != null) viewModel.clearError()
                    },
                    label = { Text("Email", color = SenecaTextSecondary) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White, unfocusedTextColor = White,
                        focusedBorderColor = SenecaYellow,
                        unfocusedBorderColor = SenecaTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (uiState.errorMessage != null) viewModel.clearError()
                    },
                    label = { Text("Password", color = SenecaTextSecondary) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White, unfocusedTextColor = White,
                        focusedBorderColor = SenecaYellow,
                        unfocusedBorderColor = SenecaTextSecondary
                    )
                )

                val error = uiState.errorMessage
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = androidx.compose.ui.graphics.Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { viewModel.signInWithEmail(email, password) },
                    enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SenecaYellow,
                        contentColor = androidx.compose.ui.graphics.Color.Black
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = androidx.compose.ui.graphics.Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "New here? A new account will be created automatically.",
                    color = SenecaTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Divider with "or"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                color = White.copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "  OR  ",
                color = SenecaTextSecondary, fontSize = 12.sp
            )
            HorizontalDivider(
                color = White.copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Guest card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Graphite.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Just exploring?",
                    color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Use the map, search places and browse the campus without an account. You can sign in later from Settings.",
                    color = SenecaTextSecondary, fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { viewModel.continueAsGuest() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
                ) {
                    Text("Continue as Guest", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
