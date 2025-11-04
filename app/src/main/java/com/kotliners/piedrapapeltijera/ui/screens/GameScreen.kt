package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    // estado local de la ronda actual
    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableStateOf(10) } // apuesta inicial mínima
    var message by remember { mutableStateOf("") }

    // Estado global (Room vía ViewModel)
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    // Lógica principal para jugar una ronda
    fun jugarCon(mov: Move) {
        // 1. Sin saldo, no puedes jugar
        if (saldo <= 0) {
            message = "Te has quedado sin monedas. Pulsa Reiniciar partida para volver a tener saldo."
            return
        }

        // 2. Apuesta mínima
        if (betAmount < 10) {
            message = "La apuesta mínima es 10 monedas."
            return
        }

        // 3. No puedes apostar más de lo que tienes
        if (betAmount > saldo) {
            message = "No puedes apostar más de tu saldo actual."
            return
        }

        // 4. Ejecutar lógica del juego
        val (r, c) = GameLogic.play(mov)
        result = r
        computerMove = c
        userMove = mov

        when (r) {
            GameResult.GANAS -> {
                viewModel.cambiarMonedas(+betAmount)
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "¡Ganaste $betAmount monedas!"
            }
            GameResult.PIERDES -> {
                viewModel.cambiarMonedas(-betAmount)
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "Perdiste $betAmount monedas."
            }
            GameResult.EMPATE -> {
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "Empate, sin cambios."
            }
        }

        // 5. Feedback especial si nos hemos quedado a 0 tras la jugada
        val saldoDespues = viewModel.monedas.value ?: 0
        if (saldoDespues <= 0) {
            message = "Te has quedado sin monedas. Pulsa Resert en configuracion para reiniciar y seguir jugando."
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
    ) {
        val scroll = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // Cabecera con Partidas / Monedas
            NeonGloboInfo(
                partidas = partidas,
                saldo = saldo,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 84.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Bloque principal del juego
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "¡Apuesta tus monedas!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoBlanco
                )

                // Selector de apuesta con + y -
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón restar apuesta
                    Button(
                        onClick = {
                            if (betAmount > 10) betAmount -= 10
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "−",
                            fontSize = 64.sp,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextoBlanco
                        )
                    }

                    // Cantidad actual apostada
                    Text(
                        text = "$betAmount monedas",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmarilloNeon
                    )

                    // Botón sumar apuesta
                    Button(
                        onClick = {
                            if (betAmount + 10 <= saldo) {
                                betAmount += 10
                            } else {
                                // feedback inmediato si intento pasarme
                                message = "No puedes apostar más de tu saldo."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = TextoBlanco
                        ),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "+",
                            fontSize = 48.sp,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextoBlanco
                        )
                    }
                }

                // Texto "Elige tu jugada"
                Text(
                    text = "Elige tu jugada:",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoBlanco
                )

                // Botones de jugada (Piedra / Papel / Tijera)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Piedra
                    Button(
                        onClick = { jugarCon(Move.PIEDRA) },
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

                    // Papel
                    Button(
                        onClick = { jugarCon(Move.PAPEL) },
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

                    // Tijera
                    Button(
                        onClick = { jugarCon(Move.TIJERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_tijera_neon),
                            contentDescription = "Tijera",
                            modifier = Modifier
                                .size(110.dp)
                                .padding(end = 4.dp) // pequeño ajuste visual
                        )
                    }
                }

                // Resultado de la ronda + estado actual
                if (result != null) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Jugador: ${userMove?.name} | Banca: ${computerMove?.name}",
                        color = TextoBlanco
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoBlanco
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Saldo actual: $saldo",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoBlanco
                    )

                    // Si el jugador está sin monedas, enseñamos botón de reinicio
                    if (saldo <= 0) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Te quedaste sin monedas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AmarilloNeon
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                // Lógica de reinicio total desde el ViewModel
                                viewModel.resetJuego()

                                // limpiar estado de la UI local
                                result = null
                                userMove = null
                                computerMove = null
                                betAmount = 10
                                message = "Has reiniciado la partida. ¡Suerte!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmarilloNeon
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 24.dp,
                                vertical = 12.dp
                            )
                        ) {
                            Text(
                                text = "Reiniciar partida",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
