package com.kotliners.piedrapapeltijera.data.local.database

import androidx.room.TypeConverter
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move

class Converters {
    @TypeConverter fun fromMove(m: Move): String = m.name
    @TypeConverter fun toMove(s: String): Move = Move.valueOf(s)

    @TypeConverter fun fromResult(r: GameResult): String = r.name
    @TypeConverter fun toResult(s: String): GameResult = GameResult.valueOf(s)
}