package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco

@Composable
fun NeonGloboInfo(
    partidas: Int,
    saldo: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .shadow(24.dp, RoundedCornerShape(22.dp), clip = false)
            .clip(RoundedCornerShape(22.dp))
            .background(RosaNeon)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Partidas",
                    color = TextoBlanco.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$partidas",
                    color = TextoBlanco,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Monedas",
                    color = TextoBlanco.copy(alpha = 0.9f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$saldo",
                    color = TextoBlanco,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}