package com.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // --- Funciones para Jugador ---

    // Inserta un nuevo jugador (usado al crear perfil)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarJugador(jugador: Jugador) // La función 'suspend fun' define una Corutina para evitar que la interfaz de la app quede en espera

    // Obtiene las estadísticas de un jugador (para mostrar en el marcador)
    // Usamos Flow para que la UI se actualice sola (reactividad)
    @Query("SELECT * FROM Jugador WHERE id_jugador = :id")
    fun getEstadisticasJugador(id: Int): Flow<Jugador> //

    // Actualiza los contadores del jugador
    @Query("UPDATE Jugador SET total_victorias = total_victorias + 1 WHERE id_jugador = :id")
    suspend fun sumarVictoria(id: Int)

    @Query("UPDATE Jugador SET total_derrotas = total_derrotas + 1 WHERE id_jugador = :id")
    suspend fun sumarDerrota(id: Int)

    @Query("UPDATE Jugador SET total_empates = total_empates + 1 WHERE id_jugador = :id")
    suspend fun sumarEmpate(id: Int)


    // --- Funciones para Historial ---

    // Inserta un nuevo registro de partida
    @Insert
    suspend fun insertarPartida(partida: HistorialPartida)

    // Obtiene todo el historial de un jugador, ordenado por el más reciente
    @Query("SELECT * FROM HistorialPartida WHERE id_jugador = :id ORDER BY id_partida DESC")
    fun getHistorialDeJugador(id: Int): Flow<List<HistorialPartida>>

}