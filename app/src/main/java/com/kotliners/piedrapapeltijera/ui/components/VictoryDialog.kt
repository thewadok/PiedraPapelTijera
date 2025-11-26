package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

// Dialogo que aparece en el momento de la victoria
@Composable
fun VictoryDialog(
    onConfirm: (saveScreenshot: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var save by remember { mutableStateOf(true) }

    // Dialogo de victoria.
    AlertDialog(

        // Si el usuario toca fuera del dialogo lo cerramos igual.
        onDismissRequest = onDismiss,

        containerColor = RosaNeon,

        // Titulo.
        title = {
            TituloSeccion(
            "¡Victoria!",
                modifier = Modifier.padding(bottom = 4.dp)
                )
                },

        // Cuerpo.
        text = {
            Column {
                Parrafo(
                    texto = "¿Quieres guardar la captura de esta victoria?",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                    ) {

                    // Checkbox para activar o desactivar
                    Checkbox(
                        checked = save,
                        onCheckedChange = { save = it }
                    )
                    Spacer(Modifier.width(8.dp))

                    Parrafo(
                        texto = "Guardar captura",
                        modifier = Modifier.weight(1f))
                }
            }
        },

        // Boton aceptar
        confirmButton = {
            NeonTextoBoton(titulo = "Aceptar") {
                onConfirm(save)
            }
        },

        // Boton cancelar
        dismissButton = {
            NeonTextoBoton(titulo = "Cancelar") {
                onDismiss()
            }
        }
    )
}