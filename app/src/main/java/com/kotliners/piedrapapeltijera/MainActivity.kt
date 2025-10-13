package com.kotliners.piedrapapeltijera

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.DialogTitle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotliners.piedrapapeltijera.ui.theme.PiedraPapelTijeraTheme

/*Rutas*/
sealed class Screen(val route: String, val title: String){
    data object Splash : Screen("splash","Splash")
    data object Home : Screen( "home", "hinicio")
    data object History : Screen( "ranking", "Ranking")
    data object Setting : Screen("setting", "Ajustes")
    data object Help : Screen("help","Ayuda")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    PiedraPapelTijeraTheme {
        val nav = rememberNavController()
        MaterialTheme {
            NavHost(
                navController = nav,
                startDestination = Screen.Home.route
            ){
                composable(Screen.Home.route) { HomeScreen()}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Piedra, Papel o Tijera", style = MaterialTheme.typography.titleLarge)
    }
}