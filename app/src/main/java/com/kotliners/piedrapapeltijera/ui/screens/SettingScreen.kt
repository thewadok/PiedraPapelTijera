package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.NeonTextoBoton
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.ui.components.Parrafo
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
//import com.kotliners.piedrapapeltijera.ui.components.Center


// Pantalla de Ajustes
@Composable
fun SettingScreen(
    nav: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .verticalScroll(scroll)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        TituloPrincipal("Ajustes de configuración")

        Spacer(Modifier.height(8.dp))

        // Reset
        NeonTextoBoton("Reset") {
            viewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(
            "Toca Reset para restablecer tu estado al estado inicial."
        )

        Spacer(Modifier.height(24.dp))

        // Rescate

        NeonTextoBoton("Rescate") {
            viewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(
            "Toca Rescate y compra 50 monedas extra para continuar."
        )

        Spacer(Modifier.height(24.dp))

        //Center("Resto de ajustes en Producto 2")
    }
}