package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

// Dialogo que aparece en el momento de la victoria
@Composable
fun VictoryDialog(
    onConfirm: (saveScreenshot: Boolean, addCalendar: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var save by remember { mutableStateOf(true) }
    var addCalendar by remember { mutableStateOf(true) }

    // Dialogo de victoria.
    AlertDialog(

        // Si el usuario toca fuera del dialogo lo cerramos igual.
        onDismissRequest = onDismiss,

        containerColor = RosaNeon,

        // Titulo.
        title = {
            TituloSeccion(
                stringResource(R.string.victory_dialog_title),
                modifier = Modifier.padding(bottom = 4.dp)
                )
                },

        // Cuerpo.
        text = {
            Column {
                Parrafo(
                    stringResource(R.string.victory_dialog_message),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Guardar captura
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                    ) {

                    // Checkbox para activar o desactivar
                    Checkbox(
                        checked = save,
                        onCheckedChange = { save = it }
                    )
                    Spacer(Modifier.width(8.dp))

                    Parrafo(
                        stringResource(R.string.victory_dialog_save_screenshot),
                        modifier = Modifier.weight(1f))
                }

                // Añadir al calendario
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = addCalendar,
                        onCheckedChange = { addCalendar = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Parrafo(
                        stringResource(R.string.victory_dialog_add_calendar),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },

        // Boton aceptar
        confirmButton = {
            NeonTextoBoton(stringResource(R.string.victory_dialog_confirm)) {
                onConfirm(save, addCalendar)
            }
        },

        // Boton cancelar
        dismissButton = {
            NeonTextoBoton(stringResource(R.string.victory_dialog_cancel)) {
                onDismiss()
            }
        }
    )
}