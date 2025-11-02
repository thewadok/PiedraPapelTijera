package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon

@Composable
fun NeonTextoBoton(titulo: String, onClick: () -> Unit) {
    Text(
        text = titulo,
        color = AmarilloNeon,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}