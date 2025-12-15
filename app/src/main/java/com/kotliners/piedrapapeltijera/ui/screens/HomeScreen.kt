package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.components.*
import com.kotliners.piedrapapeltijera.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.google.firebase.auth.FirebaseAuth
import com.kotliners.piedrapapeltijera.ui.viewmodel.ViewModelFactory

@Composable
fun HomeScreen(nav: NavHostController, viewModel: MainViewModel) {
    val scroll = rememberScrollState()
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    Box(
        Modifier
            .fillMaxSize()
            .background(FondoNegro)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // NeonGloboinfo
            NeonGloboInfo(
                partidas = partidas,
                saldo = saldo,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 84.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Botón "Jugar"
            Button(
                onClick = { nav.safeNavigate(Screen.Game.route) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(72.dp)
                    .shadow(20.dp, RoundedCornerShape(50))
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulNeon,
                    contentColor = TextoNegro
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.play_button),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Reglas básicas
            TituloPrincipal(stringResource(R.string.help_rules_title))

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.how_game_works),
                color = TextoBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            // 🔸 Líneas con íconos
            ReglaConIcono(
                iconRes = R.drawable.icono_piedra_neon,
                titulo = stringResource(R.string.rock),
                detalle = stringResource(R.string.rock_rule)
            )
            ReglaConIcono(
                iconRes = R.drawable.icono_papel_neon,
                titulo = stringResource(R.string.paper),
                detalle = stringResource(R.string.paper_rule)
            )
            ReglaConIcono(
                iconRes = R.drawable.icono_tijera_neon,
                titulo = stringResource(R.string.scissors),
                detalle = stringResource(R.string.scissors_rule)
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Resultados
            TituloSeccion(stringResource(R.string.results_title))

            Spacer(Modifier.height(8.dp))

            TextoLinea(stringResource(R.string.victory_label), stringResource(R.string.victory_desc))
            TextoLinea(stringResource(R.string.defeat_label), stringResource(R.string.defeat_desc))
            TextoLinea(stringResource(R.string.tie_label), stringResource(R.string.tie_desc))

            Spacer(Modifier.height(16.dp))

            // 🔹 Apuestas
            TituloSeccion(stringResource(R.string.bets_title))

            Spacer(Modifier.height(8.dp))

            Parrafo(stringResource(R.string.bets_description))

            Spacer(Modifier.height(16.dp))

            // 🔹 Rescate
            TituloSeccion(stringResource(R.string.rescue_title))

            Spacer(Modifier.height(8.dp))
            Parrafo(stringResource(R.string.rescue_info))

            Spacer(Modifier.height(16.dp))

            // 🔹 Cómo jugar
            TituloSeccion(stringResource(R.string.how_to_play_title))

            Spacer(Modifier.height(8.dp))
            Parrafo(stringResource(R.string.how_to_play_steps))

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReglaConIcono(
    iconRes: Int,
    titulo: String,
    detalle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = titulo,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 10.dp)
        )
        Column {
            Text(titulo, color = TextoBlanco, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(detalle, color = TextoBlanco, fontSize = 14.sp)
        }
    }
}