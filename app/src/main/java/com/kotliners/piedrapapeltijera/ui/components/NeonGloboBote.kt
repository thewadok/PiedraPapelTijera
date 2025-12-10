package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.kotliners.piedrapapeltijera.ui.theme.AzulNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoNegro

// Globo de información para mostrar el bote común en la UI
@Composable
fun NeonGloboBote(
    monedas: Int,
    modifier: Modifier = Modifier,
    titulo: String = "¡BOTE!"
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(72.dp)
            .shadow(20.dp, RoundedCornerShape(50))
            .background(AzulNeon, RoundedCornerShape(50))
            .border(3.dp, AzulNeon, RoundedCornerShape(50))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = titulo,
                color = TextoNegro,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$monedas monedas",
                color = TextoNegro,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}