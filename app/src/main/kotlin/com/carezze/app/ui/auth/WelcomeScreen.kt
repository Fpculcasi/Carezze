package com.fpculcasi.carezze.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.R
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun WelcomeScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Anonymous, is AuthUiState.Authenticated -> onNavigateToDashboard()
            else -> Unit
        }
    }

    WelcomeContent(
        isLoading = authState is AuthUiState.Loading,
        onContinueLocally = viewModel::continueLocally,
        onNavigateToLogin = onNavigateToLogin,
        onGoogleSignIn = viewModel::signInOrLinkWithGoogle,
    )
}

@Composable
internal fun WelcomeContent(
    isLoading: Boolean,
    onContinueLocally: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onGoogleSignIn: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(R.drawable.ic_app_logo), contentDescription = "logo")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Carezze",
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Monitora i bisogni delle persone a te più care",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinueLocally,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text("Continua in locale")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Accedi / Registrati")
        }

        Spacer(modifier = Modifier.height(12.dp))

        GoogleSignInButton(
            onIdTokenReceived = onGoogleSignIn,
            onError = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeContentPreview() {
    CarezzeTheme {
        WelcomeContent(
            isLoading = false,
            onContinueLocally = {},
            onNavigateToLogin = {},
            onGoogleSignIn = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun WelcomeContentLoadingPreview() {
    CarezzeTheme {
        WelcomeContent(
            isLoading = true,
            onContinueLocally = {},
            onNavigateToLogin = {},
            onGoogleSignIn = {},
        )
    }
}
