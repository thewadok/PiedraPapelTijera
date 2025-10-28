package com.data

import androidx.room.TypeConverter
class Converters {

    // Función para LEER de la BD: Convierte un String a Enum
    @TypeConverter
    fun fromStringJugada(value: String): Jugada {
        // valueOf() convierte el texto "PIEDRA" o "PAPEL" o "TIJERA" al objeto Jugada.PIEDRA o PAPEL o TIJERA
        return Jugada.valueOf(value)
    }

    @TypeConverter
    fun fromStringResultado(value: String): Resultado {
        // valueOf() convierte el texto "PIEDRA" al objeto Jugada.PIEDRA
        return Resultado.valueOf(value)
    }


    // Función para ESCRIBIR en la BD: Convierte un Enum a String
    @TypeConverter
    fun fromJugada(jugada: Jugada): String {
        // .name convierte el objeto Jugada.PIEDRA al texto "PIEDRA"
        return jugada.name
    }

    @TypeConverter
    fun fromResultado(jugada: Jugada): String {
        // .name convierte el objeto Jugada.PIEDRA al texto "PIEDRA"
        return jugada.name
    }

}