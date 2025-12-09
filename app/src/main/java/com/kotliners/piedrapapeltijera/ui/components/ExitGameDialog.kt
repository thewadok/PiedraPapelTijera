package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

@Composable
fun ExitGameDialog(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit
) {
    //Dialogo para salir del juego
    AlertDialog(
        onDismissRequest = onDismiss,

        containerColor = RosaNeon,

        // Título
        title = {
            TituloSeccion(
                text = stringResource(R.string.exit_game_title),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        text = {
            Parrafo(
                texto = stringResource(R.string.exit_game_message),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        // Botón Salir
        confirmButton = {
            NeonTextoBoton(stringResource(R.string.exit_game_confirm)) {
                onConfirmExit() }
        },

        // Botón Cancelar
        dismissButton = {
            NeonTextoBoton(stringResource(R.string.exit_game_cancel)) {
                onDismiss() }
        }
    )
}