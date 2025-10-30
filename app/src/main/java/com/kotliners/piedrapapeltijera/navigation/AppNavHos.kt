package com.kotliners.piedrapapeltijera.navigation

import androidx.navigation.NavHostController

//Extesión de NavHostController para navegar sin duplicar
fun NavHostController.safeNavigate(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}