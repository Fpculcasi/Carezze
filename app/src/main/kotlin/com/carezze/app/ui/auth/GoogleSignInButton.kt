package com.fpculcasi.carezze.ui.auth

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.fpculcasi.carezze.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInButton(
    onIdTokenReceived: (String) -> Unit,
    onError: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            scope.launch {
                launchGoogleSignIn(context, onIdTokenReceived, onError)
            }
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        Text("Accedi con Google")
    }
}

private suspend fun launchGoogleSignIn(
    context: Context,
    onIdTokenReceived: (String) -> Unit,
    onError: (String?) -> Unit,
) {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()
    val request = GetCredentialRequest(listOf(googleIdOption))

    try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onIdTokenReceived(googleCredential.idToken)
        } else {
            onError("Tipo di credenziale non supportato")
        }
    } catch (e: GetCredentialException) {
        onError(e.message)
    }
}
