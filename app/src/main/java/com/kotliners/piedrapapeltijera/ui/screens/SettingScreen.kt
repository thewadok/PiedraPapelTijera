package com.kotliners.piedrapapeltijera.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.*
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.LocaleManager

@Composable
fun SettingScreen(
    nav: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedLang by remember { mutableStateOf(LocaleManager.getSavedLanguage(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .verticalScroll(scroll)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 🔹 Título
        TituloPrincipal(text = context.getString(R.string.settings_title))

        Spacer(Modifier.height(8.dp))

        NeonTextoBoton(context.getString(R.string.reset_button)) {
            viewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))
        Parrafo(context.getString(R.string.reset_description))

        Spacer(Modifier.height(24.dp))

        NeonTextoBoton(context.getString(R.string.rescue_button)) {
            viewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))
        Parrafo(context.getString(R.string.rescue_description))

        Spacer(Modifier.height(32.dp))

        // 🌍 Selector de idioma
        TituloPrincipal(text = context.getString(R.string.language_section_title))
        Spacer(Modifier.height(12.dp))
        Parrafo(context.getString(R.string.language_instruction))
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (selectedLang != "es") {
                    selectedLang = "es"
                    LocaleManager.updateActivityLocale(activity!!, "es")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = context.getString(R.string.language_spanish))
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (selectedLang != "en") {
                    selectedLang = "en"
                    LocaleManager.updateActivityLocale(activity!!, "en")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = context.getString(R.string.language_english))
        }

        Spacer(Modifier.height(40.dp))
        Center(text = context.getString(R.string.other_settings_placeholder))
    }
}