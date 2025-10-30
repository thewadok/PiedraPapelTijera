package com.kotliners.piedrapapeltijera.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import com.kotliners.piedrapapeltijera.MyApp
import com.kotliners.piedrapapeltijera.data.repository.JugadorRepository

class MainViewModel : ViewModel() {

    private val repo = JugadorRepository(MyApp.db.jugadorDao())
    private val disposables = CompositeDisposable()

    // Expuesto para la UI
    val monedas = MutableLiveData<Int>()

    init {
        // Creamos el jugador si no existe
        // Observamos el saldo en tiempo real y lo publicamos en LiveData
        repo.ensureJugador()
            .andThen(repo.observarMonedas())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { monedas.value = it },
                onError = { e -> Log.e("MainViewModel", "Error inicializando/observando monedas", e) }
            )
            .also { disposables.add(it) }
    }

    // +n o -n para ganar/perder monedas
    fun cambiarMonedas(cantidad: Int) {
        repo.cambiarMonedas(cantidad)
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { /* OK */ },
                onError = { e -> Log.e("MainViewModel", "Error fijando monedas", e) }
            )
            .also { disposables.add(it) }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
