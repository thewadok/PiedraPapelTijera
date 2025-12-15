package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

@Composable
fun ExitGameDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
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
                text = title,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        text = {
            Parrafo(
                texto = message,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        // Botón Salir
        confirmButton = {
            NeonTextoBoton(confirmText) {
                onConfirmExit() }
        },

        // Botón Cancelar
        dismissButton = {
            NeonTextoBoton(dismissText) {
                onDismiss() }
        }
    )
}