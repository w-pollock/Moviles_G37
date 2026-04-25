package com.example.moviles_g37.ui.components

import android.media.Image
import android.view.RoundedCorner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.widthIn
import com.example.moviles_g37.R
import com.example.moviles_g37.ui.theme.DimGrey
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaTextSecondary
import com.example.moviles_g37.ui.theme.White

@Composable
fun SenecaHeader(
    title: String = "SénecaMaps",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = androidx.compose.ui.graphics.Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.seneca_cabra),
            contentDescription = "SénecaMaps goat logo",
            modifier = Modifier
                .width(34.dp)
                .height(58.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SenecaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = readOnly,
        placeholder = {
            Text(
                text = placeholder,
                color = DimGrey,
                fontSize = 14.sp
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            disabledContainerColor = White,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedTextColor = androidx.compose.ui.graphics.Color.Black,
            unfocusedTextColor = androidx.compose.ui.graphics.Color.Black,
            disabledTextColor = androidx.compose.ui.graphics.Color.Black,
            cursorColor = androidx.compose.ui.graphics.Color.Black
        )
    )
}

@Composable
fun PrimaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DimGrey,
            contentColor = White
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick()},
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Graphite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    color = SenecaTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
fun ScheduleInfoLine(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = White
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = SenecaTextPrimary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SectionSubtitle(
    text: String
) {
    Text(
        text = text,
        color = SenecaTextSecondary,
        fontSize = 16.sp
    )
}