package com.kotliners.piedrapapeltijera.data


import androidx.room.TypeConverter

class Converters {

    // Función para LEER de la BD: Convierte un String a Enum
    @TypeConverter
    fun fromStringJugada(jug: String): Jugada {
        // valueOf() convierte el texto "PIEDRA" o "PAPEL" o "TIJERA" al objeto Jugada.PIEDRA o PAPEL o TIJERA
        return Jugada.valueOf(jug)
    }

    @TypeConverter
    fun fromStringResultado(result: String): Resultado {
        // valueOf() convierte el texto "PIEDRA" al objeto Resultado.GANAS
        return Resultado.valueOf(result)
    }


    // Función para ESCRIBIR en la BD: Convierte un Enum a String
    @TypeConverter
    fun fromJugada(jugada: Jugada): String {
        // .name convierte el objeto Jugada.PIEDRA al texto "PIEDRA", "PAPEL" o "TIJERA"
        return jugada.name
    }

    @TypeConverter
    fun fromResultado(resultado: Resultado): String {
        // .name convierte el objeto Resultado.GANAS al texto "GANAS", "EMPATAS" o "PIERDES"
        return resultado.name
    }

}