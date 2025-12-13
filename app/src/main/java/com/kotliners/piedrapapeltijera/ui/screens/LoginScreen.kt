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
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.theme.TextoNegro

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit
){
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loading = false

        if (result.resultCode != Activity.RESULT_OK) {
            error = stringResource(R.string.login_cancelled)
            return@rememberLauncherForActivityResult
        }

        val data: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrBlank()) {
                error = stringResource(R.string.login_token_error)
                return@rememberLauncherForActivityResult
            }

            loading = true
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { t ->
                    loading = false
                    if (t.isSuccessful) {
                        onLoginOk()
                    } else {
                        error = stringResource(R.string.login_firebase_error)
                    }
                }

        } catch (e: ApiException) {
            error = "Error en Google Sign-In: ${e.statusCode}"
        }
    }

    fun startGoogleSignIn() {
        error = null
        loading = true

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(context, gso)

        // Forzamos selector de cuenta
        client.signOut().addOnCompleteListener {
            loading = false
            launcher.launch(client.signInIntent)
        }
    }

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
            horizontalAlignment = Alignment.Start
        ) {
            TituloPrincipal(stringResource(R.string.login_title))

            Spacer(Modifier.height(10.dp))

            Parrafo(stringResource(R.string.login_desc))

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { startGoogleSignIn() },
                enabled = !loading,
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
                    text = if (loading) stringResource(R.string.login_loading)
                    else stringResource(R.string.login_button),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}