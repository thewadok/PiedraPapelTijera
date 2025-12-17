package com.kotliners.piedrapapeltijera.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotliners.piedrapapeltijera.ui.screens.GameScreen
import com.kotliners.piedrapapeltijera.ui.screens.HelpScreen
import com.kotliners.piedrapapeltijera.ui.screens.HistoryScreen
import com.kotliners.piedrapapeltijera.ui.screens.HomeScreen
import com.kotliners.piedrapapeltijera.ui.screens.RankingScreen
import com.kotliners.piedrapapeltijera.ui.screens.SettingScreen
import com.kotliners.piedrapapeltijera.ui.screens.SplashScreen
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.ui.screens.LoginScreen
import com.kotliners.piedrapapeltijera.ui.theme.PiedraPapelTijeraTheme
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import androidx.compose.material3.SnackbarHostState

//Raiz de la app
@Composable
fun AppRoot(
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState
    ) {
    PiedraPapelTijeraTheme {
        val nav = rememberNavController()
        MaterialTheme {
            NavHost(
                navController = nav,
                startDestination = Screen.Splash.route
            ){
                composable(Screen.Splash.route) {SplashScreen(nav) }
                composable(Screen.Login.route) {
                    LoginScreen(onLoginOk = {
                    nav.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })}
                composable(Screen.Home.route) {
                    AppScaffold(nav, snackbarHostState = snackbarHostState) { HomeScreen(nav, mainViewModel)} }
                composable(Screen.History.route) { AppScaffold(nav,snackbarHostState = snackbarHostState) { HistoryScreen(mainViewModel)} }
                composable(Screen.Ranking.route) { AppScaffold(nav,snackbarHostState = snackbarHostState) { RankingScreen()} }
                composable(Screen.Setting.route) { AppScaffold(nav,snackbarHostState = snackbarHostState) { SettingScreen(nav, mainViewModel)} }
                composable(Screen.Help.route) { AppScaffold(nav,snackbarHostState = snackbarHostState) { HelpScreen()} }
                composable(Screen.Game.route) {AppScaffold(nav,snackbarHostState = snackbarHostState) { GameScreen(mainViewModel, snackbarHostState = snackbarHostState)} }
            }
        }
    }
}