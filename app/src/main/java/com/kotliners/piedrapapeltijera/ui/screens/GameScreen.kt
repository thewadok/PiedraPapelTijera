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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.kotliners.piedrapapeltijera.utils.media.rememberCaptureCurrentView
import androidx.compose.ui.platform.LocalContext
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects
import com.kotliners.piedrapapeltijera.ui.components.VictoryDialog
import com.kotliners.piedrapapeltijera.utils.calendar.rememberCalendarPermissionState
import com.kotliners.piedrapapeltijera.utils.calendar.CalendarHelper
import kotlinx.coroutines.*

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    // Estado del juego
    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableIntStateOf(10) } // apuesta inicial mínima
    var message by remember { mutableStateOf("") }
    var showVictoryDialog by remember { mutableStateOf(false) }

    // Saldo y partidas desde Room a través del ViewModel
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    // Utilidades media
    // Función para capturar la pantalla
    val captureView = rememberCaptureCurrentView()

    // Permisos de calendario
    val (requestCalendarPermissions, calendarGranted) = rememberCalendarPermissionState()

    // Contexto y scope para coroutinas
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    fun jugarCon(mov: Move) {
        // Validar apuesta con saldo actual persistido
        if (betAmount !in 1..saldo) {
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
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "¡Ganaste $betAmount monedas!"

                //Aqui agrego el efecto de sonido al ganar
                SoundEffects.playWin()

                // Mostramos dialogo con opcion de guardar la captura de pantalla
                showVictoryDialog = true
            }

            GameResult.PIERDES -> {
                viewModel.cambiarMonedas(-betAmount)
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "Perdiste $betAmount monedas."

                //Sgrego sonido al perder
                SoundEffects.playLose()
            }

            GameResult.EMPATE -> {
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = "Empate, sin cambios."
            }
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoNegro)
    ) {
        val scroll = rememberScrollState()

        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NeonGloboInfo(
                partidas = partidas,
                saldo = saldo,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 84.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            //Contenido principal
            Column(
                modifier = Modifier
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

                            SoundEffects.playClick()//Sonido al pulsarlo

                            if (betAmount > 10) betAmount -= 10
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = 125.sp,
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
                            SoundEffects.playClick()//Efecto de sonido al hacer click
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
                            text = "+",
                            fontSize = 75.sp,
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
                            SoundEffects.playClick()
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
                            SoundEffects.playClick()
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
                            SoundEffects.playClick()
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

                // Mostramos el dialogo de victoria
                if (showVictoryDialog) {
                    VictoryDialog(
                        onConfirm = { saveScreenshot, addCalendar ->
                            showVictoryDialog = false

                            scope.launch {

                                // Guardar captura
                                if (saveScreenshot) {
                                    // Capturamos la pantalla solo si el usuario quiere guardarla
                                    val screenshot = captureView()

                                    // Llamamos a la corrutina onPlayerWin cuando hay victoria
                                    viewModel.onPlayerWin(context, screenshot)
                                }

                                // Añadir al calendario
                                if (addCalendar) {
                                    if (!calendarGranted.value) {
                                        requestCalendarPermissions()
                                    } else {
                                        CalendarHelper.insertVictoryEvent(
                                            context = context,
                                            title = "Victoria en Piedra, Papel o Tijera",
                                            description = "Has ganado apostando $betAmount monedas.\nMonedas acumuladas: $saldo."

                                        )
                                    }
                                }
                            }
                        },

                        onDismiss = {
                            // Cerramos el diálogo sin hacer nada
                            showVictoryDialog = false
                        }
                    )
                }
            }
        }
    }
}