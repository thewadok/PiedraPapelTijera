package com.kotliners.piedrapapeltijera.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import com.kotliners.piedrapapeltijera.MyApp
import com.kotliners.piedrapapeltijera.data.repository.JugadorRepository
import com.kotliners.piedrapapeltijera.data.repository.PartidaRepository
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.game.GameResult
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val repo = JugadorRepository(MyApp.db.jugadorDao())
    private val historial = PartidaRepository(MyApp.db.partidaDao())
    private val disposables = CompositeDisposable()

    // Expuesto para la UI
    val monedas = MutableLiveData<Int>()
    val partidas = MutableLiveData<Int>()
    val historialPartidas = MutableLiveData<List<Partida>>()

    init {
        // Creamos el jugador si no existe
        // Observamos el saldo en tiempo real y lo publicamos en LiveData
        repo.ensureJugador()
            .andThen(repo.observarMonedas())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { monedas.value = it },
                onError = { e -> Log.e("MainViewModel", "Error inicializando/observando monedas", e) }
            )
            .also { disposables.add(it) }

        //Observamos en tiempo real el total de partidas jugadas
        historial.observarTotalPartidas()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { partidas.value = it })
            .also { disposables.add(it) }

        //Observamos historial completo de partidas
        historial.observarHistorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { historialPartidas.value = it },
                onError = { e -> Log.e("MainViewModel", "Error cargando historial", e) }
            )
            .also { disposables.add(it) }
    }

    // +n o -n para ganar/perder monedas
    fun cambiarMonedas(cantidad: Int) {
        repo.cambiarMonedas(cantidad)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error cambiando monedas", e) }
            )
            .also { disposables.add(it) }
    }

    // Fijamos un saldo exacto
    fun setMonedas(nuevoSaldo: Int) {
        repo.setMonedas(nuevoSaldo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error fijando monedas", e) }
            )
            .also { disposables.add(it) }
    }

    // Registramos partida despues de cada jugada
    fun registrarPartida(
        movJugador: Move,
        movCpu: Move,
        resultado: GameResult,
        apuesta: Int,
        latitud: Double?,
        longitud: Double?
    ) {
        historial.registrarPartida(movJugador, movCpu, resultado, apuesta, latitud, longitud)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error guardando partida", e) }
            )
            .also { disposables.add(it) }
    }

    // Restablecemos el juego al estado inicial.
    fun resetJuego() {
        setMonedas(100)
        historial.borrarHistorial()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { historialPartidas.value = emptyList() },
                onError = { e -> Log.e("MainViewModel", "Error borrando historial", e) }
            )
            .also { disposables.add(it) }
    }

    // Rescatamos al jugador si se queda sin monedas.
    fun rescate() {
        val saldoActual = monedas.value ?: 0
        if (saldoActual <= 0) {
            cambiarMonedas(50)
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
