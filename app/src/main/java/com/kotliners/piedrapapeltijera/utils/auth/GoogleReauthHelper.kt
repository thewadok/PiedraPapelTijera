package com.kotliners.piedrapapeltijera.utils.auth

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel

@Composable
fun GoogleReauthAndDelete(
    activity: Activity?,
    context: Context,
    mainViewModel: MainViewModel,
    onFinish: () -> Unit
) {
    // Comprobamos que la Activity existe
    if (activity == null) return

    // Preparamos el launcher para el resultado de Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        // Obtenemos la cuenta de Google devuelta por el intent
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.result
            val idToken = account.idToken

            // Validamos que el token exista
            if (idToken.isNullOrBlank()) {
                onFinish()
                activity.finishAffinity()
                return@rememberLauncherForActivityResult
            }

            // Creamos la credencial de Firebase a partir del token
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Reautenticamos con el ViewModel y reintentamos el borrado completo
            mainViewModel.reautenticarConCredential(
                credential = credential,
                onOk = {
                    mainViewModel.eliminarCuentaCompleta(
                        onOk = { onFinish(); activity.finishAffinity() },
                        onRequiresRecentLogin = { onFinish(); activity.finishAffinity() },
                        onError = { onFinish(); activity.finishAffinity() }
                    )
                },
                onError = {
                    onFinish()
                    activity.finishAffinity()
                }
            )
        } catch (_: Exception) {
            onFinish()
            activity.finishAffinity()
        }
    }

    // Lanzamos el flujo de Google Sign-In al entrar en el composable
    LaunchedEffect(Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(context, gso)
        launcher.launch(client.signInIntent)
    }
}