package com.kotliners.piedrapapeltijera.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.kotliners.piedrapapeltijera.ui.screens.GameScreen
import com.kotliners.piedrapapeltijera.ui.screens.HelpScreen
import com.kotliners.piedrapapeltijera.ui.screens.HistoryScreen
import com.kotliners.piedrapapeltijera.ui.screens.HomeScreen
import com.kotliners.piedrapapeltijera.ui.screens.RankingScreen
import com.kotliners.piedrapapeltijera.ui.screens.SettingScreen
import com.kotliners.piedrapapeltijera.ui.screens.SplashScreen
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.ui.theme.PiedraPapelTijeraTheme
import com.kotliners.piedrapapeltijera.ui.viewmodel.ViewModelFactory
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel

//Raiz de la app
@Composable
fun AppRoot(user: FirebaseUser, navController: NavHostController) {
    // 1. Creamos una instancia de nuestra fábrica, pasándole el usuario.
    val factory = remember(user) { ViewModelFactory(user) }

    // 2. Creamos el ViewModel usando la fábrica.
    val viewModel: MainViewModel = viewModel(factory = factory)

    PiedraPapelTijeraTheme {
        // val nav = rememberNavController()
        MaterialTheme {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ){
                composable(Screen.Splash.route) {SplashScreen(navController) }
                composable(Screen.Home.route) { AppScaffold(navController) { HomeScreen(navController, viewModel)} }
                composable(Screen.History.route) { AppScaffold(navController) { HistoryScreen(viewModel)} }
                composable(Screen.Ranking.route) { AppScaffold(navController) { RankingScreen()} }
                composable(Screen.Setting.route) { AppScaffold(navController) { SettingScreen(navController)} }
                composable(Screen.Help.route) { AppScaffold(navController) { HelpScreen()} }
                composable(Screen.Game.route) {AppScaffold(navController) { GameScreen(viewModel)
                } }
            }
        }
    }
}