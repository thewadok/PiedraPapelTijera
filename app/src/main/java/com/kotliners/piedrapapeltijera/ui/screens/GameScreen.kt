package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableStateOf(10) } // apuesta inicial mínima
    var message by remember { mutableStateOf("") }

    // Saldo desde Room a través del ViewModel
    val saldo = viewModel.monedas.observeAsState(0).value

    fun jugarCon(mov: Move) {
        // Validar apuesta con saldo actual persistido
        if (betAmount <= 0 || betAmount > saldo) {
            message = "Apuesta inválida."
            return
        }

        val (r, c) = GameLogic.play(mov)
        result = r
        computerMove = c
        userMove = mov

        when (r) {
            GameResult.GANAS -> {
                viewModel.cambiarMonedas(+betAmount)
                message = "¡Ganaste $betAmount monedas!"
            }
            GameResult.PIERDES -> {
                viewModel.cambiarMonedas(-betAmount)
                message = "Perdiste $betAmount monedas."
            }
            GameResult.EMPATE -> {
                message = "Empate, sin cambios."
            }
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoNegro)
    ) {

        //Monedas totales y posición
        Text(
            text = "$saldo monedas",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = AmarilloNeon
        )

        //Contenido principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "¡Apuesta tus monedas!",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoBlanco
            )

            //Selector de apuesta con + y -
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Botón de restar apuesta
                Button(
                    onClick = {
                        if (betAmount > 10) betAmount -= 10
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "➖",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextoBlanco
                    )
                }

                // Muestra la cantidad actual
                Text(
                    text = "$betAmount monedas",
                    style = MaterialTheme.typography.titleLarge,
                    color = AmarilloNeon
                )

                // Botón de sumar apuesta
                Button(
                    onClick = {
                        if (betAmount + 10 <= saldo)
                            betAmount += 10
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextoBlanco
                    ),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "➕",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextoBlanco
                    )
                }
            }

            Text(
                text = "Elige tu jugada:",
                style = MaterialTheme.typography.titleMedium,
                color = TextoBlanco
            )

            //Botones de jugada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Piedra
                Button(
                    onClick = {
                        jugarCon(Move.PIEDRA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_piedra_neon),
                        contentDescription = "Piedra",
                        modifier = Modifier.size(95.dp)
                    )
                }

                //Papel
                Button(
                    onClick = {
                        jugarCon(Move.PAPEL)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_papel_neon),
                        contentDescription = "Papel",
                        modifier = Modifier.size(95.dp)
                    )
                }

                //Tijera (tamaño ajustado)
                Button(
                    onClick = {
                        jugarCon(Move.TIJERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_tijera_neon),
                        contentDescription = "Tijera",
                        modifier = Modifier
                            .size(110.dp)
                            .padding(end = 4.dp) // pequeño margen para centrar mejor
                    )
                }
            }

            // Resultado y saldo
            if (result != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Jugador: ${userMove?.name} | Banca: ${computerMove?.name}",
                    color = TextoBlanco
                )
                Text(
                    message,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoBlanco
                )
                Text(
                    "Saldo actual: $saldo",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoBlanco
                )
            }
        }
    }
}