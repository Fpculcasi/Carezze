package com.fpculcasi.carezze.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val resetEmailSent by viewModel.resetEmailSent.collectAsStateWithLifecycle()

    LaunchedEffect(resetEmailSent) {
        if (resetEmailSent) {
            viewModel.clearResetEmailSent()
            onNavigateBack()
        }
    }

    ForgotPasswordContent(
        errorMessage = errorMessage,
        onSendReset = viewModel::sendPasswordReset,
        onClearError = viewModel::clearError,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ForgotPasswordContent(
    errorMessage: String?,
    onSendReset: (email: String) -> Unit,
    onClearError: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recupera password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Inserisci l'indirizzo email del tuo account. Ti invieremo un link per reimpostare la password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    onClearError()
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSendReset(email.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank(),
            ) {
                Text("Invia link di recupero")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordContentPreview() {
    CarezzeTheme {
        ForgotPasswordContent(
            errorMessage = null,
            onSendReset = {},
            onClearError = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Con errore")
@Composable
private fun ForgotPasswordContentErrorPreview() {
    CarezzeTheme {
        ForgotPasswordContent(
            errorMessage = "Nessun account trovato con questa email",
            onSendReset = {},
            onClearError = {},
            onNavigateBack = {},
        )
    }
}
