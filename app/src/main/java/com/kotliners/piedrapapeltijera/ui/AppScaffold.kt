package com.kotliners.piedrapapeltijera.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.BrandBar
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.theme.TextoNegro
import com.kotliners.piedrapapeltijera.utils.system.exitGame


@Composable
fun MusicToggleButton() {
    // Sacamos la Activity actual (MainActivity) desde el contexto
    val context = LocalContext.current
    val activity = context as? MainActivity

    // Estado para saber si la musica está encendida o apagada
    var isOn by remember {
        mutableStateOf(activity?.isMusicRunning() ?: false)
    }

    IconButton(
        onClick = {
            // Al pulsar cambiamos el estado de la musica
            activity?.toggleMusic()
            // Actualizamos el estado local para cambiar el icono
            isOn = activity?.isMusicRunning() ?: false
        }
    ) {
        // Elegimos qué imagen mostrar según si está encendido o apagado
        val iconRes = if (isOn) {
            R.drawable.altavoz_on    // musica sonando
        } else {
            R.drawable.altavoz_off   // musica parada
        }

           /* isOn = !isOn
            // Al pulsar cambiamos el estado de la musica
            activity?.toggleMusic()

        }
    ) {
        // Elegimos qué imagen mostrar según si está encendido o apagado
        val iconRes = if (isOn) R.drawable.altavoz_on else R.drawable.altavoz_off
*/
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = if (isOn) "Desactivar música" else "Activar música",
            tint = TextoBlanco
        )
    }
}


@Composable
fun ExitGameButton() {
    val context = LocalContext.current
    val activity = context as? Activity
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        com.kotliners.piedrapapeltijera.ui.components.ExitGameDialog(
            onConfirmExit = {
                showDialog = false
                activity?.exitGame()
                            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Salir del juego",
            tint = TextoBlanco
        )
    }
}

//Scaffold comun para todas las pantallas menos la de Bienvenida
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    nav: NavHostController,
    content: @Composable () -> Unit
) {
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Screen.Home.route

    val items = listOf(
        Screen.Home,
        Screen.History,
        Screen.Ranking,
        Screen.Setting,
        Screen.Help
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BrandBar() },
                actions = {
                    // Altavoz + botón salir
                    MusicToggleButton()
                    ExitGameButton()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoNegro,
                    titleContentColor = TextoBlanco,
                    navigationIconContentColor = TextoBlanco
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = FondoNegro
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = (current == screen.route),
                        onClick = { nav.safeNavigate(screen.route) },
                        icon = {
                            when (screen) {
                                Screen.Home -> Icon(Icons.Default.Home, contentDescription = null)
                                Screen.History -> Icon(
                                    Icons.Default.History,
                                    contentDescription = null
                                )

                                Screen.Ranking -> Icon(
                                    Icons.Default.Leaderboard,
                                    contentDescription = null
                                )

                                Screen.Setting -> Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )

                                Screen.Help -> Icon(
                                    Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null
                                )

                                else -> {}
                            }
                        },
                        label = { Text(stringResource(screen.titleResId)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextoNegro,
                            selectedTextColor   = TextoBlanco,
                            indicatorColor      = AmarilloNeon,
                            unselectedIconColor = TextoBlanco,
                            unselectedTextColor = TextoBlanco
                        )
                    )
                }
            }
        }
    ) { inner ->
        Box(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(FondoNegro)

        ) {
            content()
        }
    }
}
