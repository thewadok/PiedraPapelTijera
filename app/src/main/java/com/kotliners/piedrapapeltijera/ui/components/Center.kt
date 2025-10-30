package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco

//Componente visual para centrar el texto a pantalla completa
@Composable
fun Center(text: String) {
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text,
            style = MaterialTheme.typography.titleLarge,
            color = TextoBlanco
        )
    }
}