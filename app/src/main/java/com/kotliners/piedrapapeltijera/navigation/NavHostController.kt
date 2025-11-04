package com.kotliners.piedrapapeltijera.navigation

import androidx.navigation.NavHostController

// Navega evitando duplicar la misma pantalla en el back stack
fun NavHostController.safeNavigate(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
