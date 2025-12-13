package com.kotliners.piedrapapeltijera.ui.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kotliners.piedrapapeltijera.R
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {

    // Se obtiene una referencia a la autenticación de Firebase.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Se configuran las opciones de inicio de sesión de Google.
    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    // Se crea un cliente de Google Sign-In con las opciones anteriores.
    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)


    // Devuelve el Intent que la UI debe lanzar para iniciar el flujo de login de Google.
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Procesa el resultado devuelto por el Intent de Google Sign-In.
     * Si tiene éxito, autentica al usuario en Firebase y lo devuelve.
     * Si falla (por ejemplo si el usuario cancela), devuelve null.
     */
    suspend fun handleSignInResult(data: Intent?): FirebaseUser? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            // Obtiene la cuenta de Google del resultado.
            val account = task.result
            // Crea una credencial de Firebase a partir del token de la cuenta de Google.
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            // Usa la credencial para autenticarse en Firebase.
            val authResult = auth.signInWithCredential(credential).await()
            // Devuelve el usuario de Firebase.
            authResult.user
        } catch (e: Exception) {
            // Si algo falla, devuelve null.
            null
        }
    }


    // Cierra la sesión tanto en Firebase como en Google.
    suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }

    /**
     * Devuelve el usuario de Firebase que está actualmente autenticado,
     * o null si no hay nadie.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}