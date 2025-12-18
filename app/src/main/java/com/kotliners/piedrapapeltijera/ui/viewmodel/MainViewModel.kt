package com.kotliners.piedrapapeltijera.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import com.kotliners.piedrapapeltijera.data.repository.local.JugadorRepository
import com.kotliners.piedrapapeltijera.data.repository.local.PartidaRepository
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.utils.victory.VictoryManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import com.kotliners.piedrapapeltijera.data.repository.remote.AuthRepository
import com.google.firebase.auth.AuthCredential

class MainViewModel(

    // Repositorio local jugador
    private val repo: JugadorRepository,

    // Repositorio local de partidas
    private val historial: PartidaRepository,

    // Repo remoto de autenticación
    private val authRepo: AuthRepository


) : ViewModel() {

    // Contenedor para cancelar todas las suscripciones RxJava al cerrar el ViewModel
    private val disposables = CompositeDisposable()

    // Clase encargada de manejar acciones al ganar
    private val victoryManager: VictoryManager = VictoryManager()

    // Expuesto para la UI
    val monedas = MutableLiveData<Int>()
    val partidas = MutableLiveData<Int>()
    val historialPartidas = MutableLiveData<List<Partida>>()

    // Nombre del jugador
    private val _nombreJugador = MutableLiveData("")
    val nombreJugador: MutableLiveData<String> = _nombreJugador

    init {

        // Valor inicial seguro mientras se carga Firebase
        _nombreJugador.value = "Un jugador"

        // Creamos el jugador si no existe
        // Observamos el saldo en tiempo real y lo publicamos en LiveData
        repo.ensureJugador()
            .andThen(repo.observarMonedas())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { monedas.value = it },
                onError = { e ->
                    Log.e(
                        "MainViewModel",
                        "Error inicializando/observando monedas",
                        e
                    )
                }
            )
            .also { disposables.add(it) }

        /*
        //Observamos en tiempo real el total de partidas jugadas
        historial.observarTotalPartidas()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { partidas.value = it })
            .also { disposables.add(it) }

         */

        //Observamos historial completo de partidas
        historial.observarHistorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { historialPartidas.value = it },
                onError = { e -> Log.e("MainViewModel", "Error cargando historial", e) }
            )
            .also { disposables.add(it) }

        syncJugadorDesdeFirebase()
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

        // Actualizamos la UI
        monedas.postValue(nuevoSaldo)

        repo.setMonedas(nuevoSaldo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error fijando monedas", e) }
            )
            .also { disposables.add(it) }
    }

    // Fijamos un número exacto de partidas
    fun setPartidas(nuevoTotal: Int) {

        // Actualizamos la UI
        partidas.postValue(nuevoTotal)

        repo.setPartidas(nuevoTotal)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error fijando partidas", e) }
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
                onComplete = {

                    //  Actualizamos el contador en pantalla inmediatamente
                    val actuales = partidas.value ?: 0
                    partidas.postValue(actuales + 1)

                    // Incrementar partidas en Firebase
                    val uid = authRepo.currentUid()
                    if (uid != null) {
                        authRepo.sumarPartida(
                            uid = uid,
                            onError = { e ->
                                Log.e(
                                    "MainViewModel",
                                    "Error sumando partidas en Firebase",
                                    e
                                )
                            }
                        )
                    }
                },
                onError = { e -> Log.e("MainViewModel", "Error guardando partida", e) }
            )
            .also { disposables.add(it) }
    }

    // Restablecemos el juego al estado inicial.
    fun resetJuego() {

        // Local
        setMonedas(100)
        setPartidas(0)

        historial.borrarHistorial()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { historialPartidas.value = emptyList() },
                onError = { e -> Log.e("MainViewModel", "Error borrando historial", e) }
            )
            .also { disposables.add(it) }

        // Remoto
        syncReset()
    }

    // Rescatamos al jugador si se queda sin monedas.
    fun rescate() {
        val saldoActual = monedas.value ?: 0
        if (saldoActual <= 0) {
            // Aplicamos el rescate sincronizando el saldo en local y en remoto.
            aplicarRescate()
        }
    }

    /*
    Creamos la función onPlayerWin para gestionar lo que ocurre cuando el jugador gana una partida.
    Desde aquí llamamos a VictoryManager.
     */
    fun onPlayerWin(
        context: Context,
        screenshot: Bitmap
    ) {
        viewModelScope.launch {
            victoryManager.handleVictory(context, screenshot)
        }
    }

    // Limpiamos todas las suscripciones RxJava para evitar fugas de memoria.
    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    // Sincronizamos el resultado de una partida tanto en local (Room) como en remoto (Firebase).
    private fun syncResultado(
        deltaMonedas: Int,
        victoria: Boolean
    ) {
        // Local (Room)
        cambiarMonedas(deltaMonedas)

        // Remoto (Firebase)
        val uid = authRepo.currentUid() ?: return

        authRepo.sumarMonedas(
            uid = uid,
            delta = deltaMonedas,
            onError = { Log.e("MainViewModel", "Error actualizando monedas en Firebase") }
        )

        if (victoria) {
            authRepo.sumarVictoria(
                uid = uid,
                onError = { Log.e("MainViewModel", "Error sumando victoria en Firebase") }
            )
        } else {
            authRepo.sumarDerrota(
                uid = uid,
                onError = { Log.e("MainViewModel", "Error sumando derrota en Firebase") }
            )
        }
    }

    // Sincronizamos el reset del juego en firebase, monedas a 100, victorias y derrotas a 0
    private fun syncReset() {

        val uid = authRepo.currentUid() ?: return

        authRepo.setMonedas(
            uid = uid,
            saldo = 100,
            onError = { Log.e("MainViewModel", "Error reseteando monedas en Firebase") }
        )

        authRepo.resetStats(
            uid = uid,
            onError = { Log.e("MainViewModel", "Error reseteando stats en Firebase") }
        )

        authRepo.resetPartidas(
            uid = uid,
            onError = { Log.e("MainViewModel", "Error reseteando partidas en Firebase") }
        )

    }

    // Sincronizamos rescate, sumamos monedas en local y remoto, sin tocar victorias/derrotas.
    private fun syncRescate() {
        // Local
        cambiarMonedas(50)

        // Remoto
        val uid = authRepo.currentUid() ?: return
        authRepo.sumarMonedas(
            uid = uid,
            delta = 50,
            onError = { Log.e("MainViewModel", "Error aplicando rescate en Firebase") }
        )
    }

    // Sincronizamos monedas en Local tras volver a iniciar sesión
    fun syncJugadorDesdeFirebase() {
        val uid = currentUid() ?: return

        authRepo.obtenerJugador(
            uid = uid,
            onOk = { jugador ->
                jugador ?: return@obtenerJugador

                // Monedas
                val monedasRemotas = jugador.monedas

                if (monedasRemotas != null) {
                    setMonedas(monedasRemotas) // pisa el 100 local
                }

                // Partidas
                jugador.partidas?.let { setPartidas(it) }

                // Sincronizamos nombre
                jugador.nombre?.let {
                    _nombreJugador.postValue(it)
                }
            },
            onError = {
                Log.e("MainViewModel", "Error leyendo jugador remoto para sincronizar monedas")
            }
        )
    }


    // Sincronizamos en remoto la adquisición del bote
    fun aplicarPremioComun(premio: Int) {
        if (premio <= 0) return

        // Local
        cambiarMonedas(premio)

        // Remoto
        val uid = authRepo.currentUid() ?: return
        authRepo.sumarMonedas(
            uid = uid,
            delta = premio,
            onError = { Log.e("MainViewModel", "Error sumando premio común en Firebase") }
        )
    }

    // Aplica una victoria sumando la apuesta y marcando victoria
    fun aplicarVictoria(apuesta: Int) =
        syncResultado(deltaMonedas = apuesta, victoria = true)

    // Aplica una derrota restando la apuesta y marcando derrota
    fun aplicarDerrota(apuesta: Int) =
        syncResultado(deltaMonedas = -apuesta, victoria = false)

    // Aplicamos rescate sumando 50 monedas
    fun aplicarRescate() = syncRescate()

    // Obtenemos el UID del usuario logueado
    fun currentUid(): String? = authRepo.currentUid()

    fun eliminarCuentaCompleta(
        onOk: () -> Unit,
        onRequiresRecentLogin: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Obtenemos el UID del usuario autenticado
        val uid = authRepo.currentUid() ?: run {
            onError(IllegalStateException("No hay uid"))
            return
        }

        // 1) Borramos los datos del jugador en Firebase Realtime Database
        authRepo.borrarJugadorRemoto(
            uid = uid,
            onOk = {
                // 2) Borramos los datos locales en Room (jugador e historial)
                repo.borrarJugador() // Eliminamos el jugador local
                    .andThen(historial.borrarHistorial()) // Eliminamos el historial de partidas
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onComplete = {
                            // 3) Borramos la cuenta del usuario en Firebase Authentication
                            authRepo.borrarCuentaAuth(
                                onOk = { onOk() },
                                onRequiresRecentLogin = { onRequiresRecentLogin() },
                                onError = { e -> onError(e) }
                            )
                        },
                        // Gestionamos errores al borrar datos locales
                        onError = { e -> onError(Exception(e)) }
                    )
                    .also { disposables.add(it) }
            },
            // Gestionamos errores al borrar los datos remotos
            onError = {
                onError(Exception("Error borrando jugador remoto"))
            }
        )
    }

    fun reautenticarConCredential(
        credential: AuthCredential,
        onOk: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        authRepo.reautenticarConCredential(
            credential = credential,
            onOk = onOk,
            onError = onError
        )
    }
}

