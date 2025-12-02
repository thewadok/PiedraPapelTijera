package com.kotliners.piedrapapeltijera.utils.locale

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.Move

@Composable
fun moveLabel(move: Move?): String {
    return when (move) {
        Move.PIEDRA -> stringResource(R.string.move_rock)
        Move.PAPEL  -> stringResource(R.string.move_paper)
        Move.TIJERA -> stringResource(R.string.move_scissors)
        null        -> "—"
    }
}