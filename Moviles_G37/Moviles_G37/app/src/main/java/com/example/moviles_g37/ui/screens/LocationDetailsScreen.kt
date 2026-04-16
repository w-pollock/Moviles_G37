package com.example.moviles_g37.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles_g37.R
import com.example.moviles_g37.ui.components.SenecaHeader
import com.example.moviles_g37.ui.theme.Graphite
import com.example.moviles_g37.ui.theme.Onyx
import com.example.moviles_g37.ui.theme.SenecaTextPrimary
import com.example.moviles_g37.ui.theme.SenecaTextSecondary
import com.example.moviles_g37.ui.theme.SenecaYellow
import com.example.moviles_g37.ui.theme.White
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LocationDetailsScreen(
    onBackClick: () -> Unit,
    locationDetailsViewModel: LocationDetailsViewModel = viewModel()
) {

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

            SenecaHeader(
                title = "SénecaMaps",
                showBackButton = true,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(White)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ML-603",
                    color = androidx.compose.ui.graphics.Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Graphite)
                .padding(16.dp)
        ) {
            Text(
                text = "Puntos de interes cercanos",
                color = SenecaTextSecondary,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            NearbyPoiItem("Cajero davivienda – 5to piso ML")
            Spacer(modifier = Modifier.height(10.dp))
            NearbyPoiItem("Cosechas – Terraza del ML")
            Spacer(modifier = Modifier.height(10.dp))
            NearbyPoiItem("La Galleteria – 5to Piso ML")

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Clase Actual",
                color = SenecaTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Constr. Aplicaciones Móviles",
                color = SenecaTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                InfoLine(
                    icon = Icons.Outlined.Schedule,
                    text = "9:30 - 11:00"
                )

                InfoLine(
                    icon = Icons.Outlined.PersonOutline,
                    text = "Mario Linares"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = White.copy(alpha = 0.45f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "CALENDARIO SEMANAL",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.calendar_photo),
                contentDescription = "Calendario semanal",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun NearbyPoiItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
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
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = SenecaTextPrimary,
            fontSize = 16.sp
        )
    }
}