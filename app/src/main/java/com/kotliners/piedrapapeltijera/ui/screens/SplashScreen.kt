package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

//Pantalla de Bienvenida
@Composable
fun SplashScreen(nav: NavHostController){
    LaunchedEffect(Unit) {
        delay(3000)

        val user = FirebaseAuth.getInstance().currentUser
        val destino = if (user != null) Screen.Home.route else Screen.Login.route

        nav.navigate(destino){
            popUpTo(Screen.Splash.route)  { inclusive = true }
            launchSingleTop = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(FondoNegro),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_splash),
            contentDescription = "Bienvenida Kotliners",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}