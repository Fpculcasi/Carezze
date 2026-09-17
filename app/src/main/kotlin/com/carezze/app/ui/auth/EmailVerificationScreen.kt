package com.fpculcasi.carezze.ui.auth

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun EmailVerificationScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val emailVerified by viewModel.emailVerified.collectAsStateWithLifecycle()
    val resendCooldown by viewModel.resendCooldown.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(emailVerified) {
        if (emailVerified) onNavigateToDashboard()
    }

    EmailVerificationContent(
        resendCooldown = resendCooldown,
        errorMessage = errorMessage,
        onCheckVerification = viewModel::checkEmailVerified,
        onResend = viewModel::resendVerificationEmail,
        onClearError = viewModel::clearError,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmailVerificationContent(
    resendCooldown: Int,
    errorMessage: String?,
    onCheckVerification: () -> Unit,
    onResend: () -> Unit,
    onClearError: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verifica email") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Abbiamo inviato un link di verifica alla tua email.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Apri l'email e clicca sul link, poi torna qui e premi il bottone.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    onClearError()
                    onCheckVerification()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Ho verificato la mia email")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onResend,
                modifier = Modifier.fillMaxWidth(),
                enabled = resendCooldown == 0,
            ) {
                Text(
                    if (resendCooldown > 0) "Rinvia (${resendCooldown}s)" else "Rinvia email di verifica"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailVerificationContentPreview() {
    CarezzeTheme {
        EmailVerificationContent(
            resendCooldown = 0,
            errorMessage = null,
            onCheckVerification = {},
            onResend = {},
            onClearError = {},
        )
    }
}

@Preview(showBackground = true, name = "Cooldown attivo")
@Composable
private fun EmailVerificationCooldownPreview() {
    CarezzeTheme {
        EmailVerificationContent(
            resendCooldown = 42,
            errorMessage = "Email non ancora verificata. Controlla la tua casella di posta.",
            onCheckVerification = {},
            onResend = {},
            onClearError = {},
        )
    }
}
