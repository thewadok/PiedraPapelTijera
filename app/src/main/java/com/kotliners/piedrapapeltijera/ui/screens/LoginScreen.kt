package com.kotliners.piedrapapeltijera.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.ui.components.Parrafo
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import com.kotliners.piedrapapeltijera.ui.theme.AzulNeon
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoNegro
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.ui.viewmodel.LoginViewModel


@Composable
fun LoginScreen(
    onLoginOk: () -> Unit
){
    // ViewModel del login
    val vm: LoginViewModel = viewModel()

    // Estado de la UI
    val uiState = vm.uiState

    // Contexto de Android
    val context = LocalContext.current

    // Firebase Auth
    val auth = remember { FirebaseAuth.getInstance() }

    // Launcher para el resultado del login con Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        vm.setLoading(false)

        // Si el usuario cancela el login
        if (result.resultCode != Activity.RESULT_OK) {
            vm.setError(R.string.login_cancelled)
            return@rememberLauncherForActivityResult
        }

        val data: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            // Error si no hay token
            if (idToken.isNullOrBlank()) {
                vm.setError(R.string.login_token_error)
                return@rememberLauncherForActivityResult
            }

            // Autenticación con Firebase
            vm.setLoading(true)
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { t ->
                    vm.setLoading(false)

                    if (t.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid
                        val displayName = user?.displayName

                        // Error inesperado
                        if (uid.isNullOrBlank()) {
                            vm.setError(R.string.login_firebase_error)
                            return@addOnCompleteListener
                        }

                        // Delegamos la lógica al ViewModel
                        vm.onGoogleLoginSuccess(
                            uid = uid,
                            displayName = displayName,
                            onLoginOk = onLoginOk
                        )
                    } else {
                        vm.setError(R.string.login_firebase_error)
                    }
                }

        } catch (e: ApiException) {
            vm.setError(R.string.login_google_error)
        }
    }

    // Inicia el flujo de login con Google
    fun startGoogleSignIn() {
        vm.clearError()
        vm.setLoading(true)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(context, gso)

        // Forzamos selector de cuenta
        client.signOut().addOnCompleteListener {
            vm.setLoading(false)
            launcher.launch(client.signInIntent)
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titulo
            TituloPrincipal(stringResource(R.string.login_title))

            Spacer(Modifier.height(10.dp))

            // Descripción
            Parrafo(stringResource(R.string.login_desc))

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { startGoogleSignIn() },
                enabled = !uiState.loading,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(72.dp)
                    .shadow(20.dp, RoundedCornerShape(50))
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulNeon,
                    contentColor = TextoNegro
                )
            ) {
                Text(
                    text = if (uiState.loading) stringResource(R.string.login_loading)
                    else stringResource(R.string.login_button),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Mensaje de error
            uiState.errorRes?.let { resId ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(resId),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            // Diálogo obligatorio para elegir nombre
            if (uiState.showNameDialog) {
                AlertDialog(
                    onDismissRequest = { /* no cerrar */ },
                    title = {
                        Text(stringResource(R.string.choose_name_title))
                    },
                    text = {
                        OutlinedTextField(
                            value = uiState.suggestedName,
                            onValueChange = { vm.onNameChanged(it) },
                            label = {
                                Text(stringResource(R.string.choose_name_hint))
                            },
                            isError = uiState.nameError
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                vm.onConfirmName(
                                    nombre = uiState.suggestedName,
                                    onLoginOk = onLoginOk
                                )
                            }
                        ) {
                            Text(stringResource(R.string.continue_button))
                        }
                    }
                )
            }
        }
    }
}