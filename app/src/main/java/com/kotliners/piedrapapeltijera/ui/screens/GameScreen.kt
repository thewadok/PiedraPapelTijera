package com.kotliners.piedrapapeltijera.ui.screens

import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.location.LocationManager
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableStateOf(10) } // apuesta inicial mínima
    var message by remember { mutableStateOf("") }

    // Saldo y partidas desde Room a través del ViewModel
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value


    // --- GESTIÓN DE UBICACIÓN ---
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val coroutineScope = rememberCoroutineScope()

    fun jugarCon(mov: Move, location: Location?) {
        // Validar apuesta con saldo actual persistido
        if (betAmount <= 0 || betAmount > saldo) {
            message = "Apuesta inválida."
            return
        }

        val (r, c) = GameLogic.play(mov)
        result = r
        computerMove = c
        userMove = mov
        viewModel.registrarPartida(mov, c, r, betAmount, location?.latitude, location?.longitude)
        message = when (r) {
            GameResult.GANAS -> "¡Ganaste $betAmount monedas!"
            GameResult.PIERDES -> "Perdiste $betAmount monedas."
            else -> "Empate, sin cambios."
        }
    }

    // Esta es la función que se ejecutará después de que el usuario responda a la solicitud de permiso.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si el usuario concede el permiso, obtenemos la ubicación y jugamos.
            coroutineScope.launch {
                val location = locationManager.getCurrentLocation()
                jugarCon(userMove!!, location) // Volvemos a llamar a jugarCon con la ubicación.
            }
        } else {
            // Si el usuario deniega el permiso, jugamos sin ubicación.
            jugarCon(userMove!!, null)
        }
    }



    fun iniciarJuego(mov: Move) {
        userMove = mov // Guardamos la jugada del usuario.
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
                            iniciarJuego(Move.PIEDRA)
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
                            iniciarJuego(Move.PAPEL)
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
                            iniciarJuego(Move.TIJERA)
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
}