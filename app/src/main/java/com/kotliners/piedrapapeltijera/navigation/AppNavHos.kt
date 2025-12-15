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

// Navegación de logout para borrar toda la pila
fun NavHostController.logoutTo(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}