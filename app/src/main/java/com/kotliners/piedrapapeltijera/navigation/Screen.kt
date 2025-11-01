package com.kotliners.piedrapapeltijera.navigation

//Rutas
sealed class Screen(val route: String, val title: String){
    data object Splash : Screen("splash","Splash")
    data object Home : Screen( "home", "Home")
    data object History : Screen( "history", "Historial")
    data object Ranking : Screen( "ranking", "Ranking")
    data object Setting : Screen("setting", "Ajustes")
    data object Help : Screen("help","Ayuda")

    data object Game : Screen("game","Juego")
}