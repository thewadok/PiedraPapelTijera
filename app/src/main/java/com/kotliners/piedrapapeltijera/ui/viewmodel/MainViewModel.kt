package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.kotliners.piedrapapeltijera.MyApp

class MainViewModel : ViewModel() {
    private val dao = MyApp.db.jugadorDao()

    init {
        inicializarJugador()
    }
    private fun inicializarJugador() {
        dao.obtenerJugador()
            .subscribeBy(
                onSuccess = { /* Jugador ya existe */ },
                onComplete = {
                    // No existe → crear jugador con monedas iniciales
                    dao.insertarJugador(Jugador())
                        .subscribeBy()
                },
                onError = {}
            )
    }
}
