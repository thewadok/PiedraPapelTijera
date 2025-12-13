package com.kotliners.piedrapapeltijera.navigation

import com.kotliners.piedrapapeltijera.R
//Rutas
sealed class Screen(val route: String, val titleResId: Int){

    data object Splash : Screen("splash",R.string.app_name)
    data object Login  : Screen("login",  R.string.login_title)
    data object Home : Screen( "home", R.string.title_home)
    data object History : Screen( "history", R.string.history_title)
    data object Ranking : Screen( "ranking", R.string.results_title)
    data object Setting : Screen("setting", R.string.settings_title)
    data object Help : Screen("help",R.string.help)

    data object Game : Screen("game",R.string.play_button)
}