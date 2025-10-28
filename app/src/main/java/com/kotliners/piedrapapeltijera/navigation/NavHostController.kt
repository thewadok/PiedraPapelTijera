package com.kotliners.piedrapapeltijera.navigation

import androidx.navigation.NavHostController

//Con esta clase podemos navegar sin duplicaciones en caso de estar en la misma ruta
fun NavHostController.safeNavigate(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}