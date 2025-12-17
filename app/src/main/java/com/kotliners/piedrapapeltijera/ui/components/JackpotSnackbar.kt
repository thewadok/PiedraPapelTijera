package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon

@Composable
fun JackpotSnackbar(snackbarData: SnackbarData) {
    Snackbar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = TextoBlanco
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(20.dp, RoundedCornerShape(22.dp), clip = false)
                    .clip(RoundedCornerShape(22.dp))
                    .background(RosaNeon)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TÍTULO (nombre)
                    Text(
                        text = snackbarData.visuals.actionLabel.orEmpty(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmarilloNeon,
                        maxLines = 1
                    )

                    // TEXTO (premio)
                    Text(
                        text = snackbarData.visuals.message,
                        fontSize = 18.sp,
                        color = TextoBlanco,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
