package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco

@Composable
fun LoginScreen(
    onSignInClick: () -> Unit // Una función que se ejecutará cuando se pulse el botón
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Bienvenido a Piedra, Papel, Tijera",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoBlanco
            )

            Button(
                onClick = onSignInClick, // Llama a la función que nos pasan como parámetro
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Iniciar sesión con Google")
            }
        }
    }
}