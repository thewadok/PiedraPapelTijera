package com.kotliners.piedrapapeltijera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.AppRoot

//Activity principal
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )
        setContent {
            AppRoot()
        }
    }
}