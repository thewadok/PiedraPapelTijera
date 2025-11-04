package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.theme.AzulNeon
import com.kotliners.piedrapapeltijera.ui.components.TextoLinea
import com.kotliners.piedrapapeltijera.ui.components.Parrafo
import com.kotliners.piedrapapeltijera.ui.components.TituloSeccion
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
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



//Pantalla de Inicio
@Composable
fun HomeScreen(
    nav: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    // Diseño del HOME
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

            // Botón "Jugar"
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
                        "Jugar",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Reglas
            TituloPrincipal("Reglas Básicas")

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Cómo funciona el juego",
                color = TextoBlanco,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            // Línea Piedra
            ReglaConIcono(
                iconRes = R.drawable.icono_piedra_neon,
                titulo = "Piedra",
                detalle = "Vence a Tijera"
            )
            // Línea Papel
            ReglaConIcono(
                iconRes = R.drawable.icono_papel_neon,
                titulo = "Papel",
                detalle = "Vence a Piedra"
            )
            // Línea Tijera
            ReglaConIcono(
                iconRes = R.drawable.icono_tijera_neon,
                titulo = "Tijera",
                detalle = "Vence a Papel"
            )

            Spacer(Modifier.height(16.dp))

            // Resultados
            TituloSeccion("Resultados")

            Spacer(Modifier.height(8.dp))

            TextoLinea("Victoria:", "Ganas tu apuesta.")
            TextoLinea("Derrota:", "Pierdes tu apuesta.")
            TextoLinea("Empate:", "No ganas ni pierdes.")

            Spacer(Modifier.height(16.dp))

            // Apuestas
            TituloSeccion("Apuestas")

            Spacer(Modifier.height(8.dp))

            Parrafo(
                """
• Apuesta mínima: 10 monedas
• Apuesta máxima: tu saldo disponible
• No puedes apostar más de lo que tienes
""".trimIndent()
            )

            Spacer(Modifier.height(16.dp))

            // Rescate
            TituloSeccion("Rescate")

            Spacer(Modifier.height(8.dp))

            Parrafo(
                """
Sin saldo, pero no sin suerte.

Ve a Ajustes, toca Rescate y compra 50 monedas extra para continuar.
""".trimIndent()
            )

            Spacer(Modifier.height(16.dp))

            TituloSeccion("Cómo Jugar")

            Spacer(Modifier.height(8.dp))

            Parrafo(
                """
1) Elige tu apuesta: usa + y − para ajustar la cantidad.

2) Selecciona tu jugada: Piedra, Papel o Tijera.

3) Pulsa el icono para jugar.

4) La banca selecciona su jugada al azar.

5) Comprueba el resultado y tu saldo acumulado al instante.
""".trimIndent()
            )

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