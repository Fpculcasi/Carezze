package com.fpculcasi.carezze.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.ui.auth.AuthUiState
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRedeemInvitation: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val user by viewModel.userState.collectAsStateWithLifecycle()
    val deleteError by viewModel.deleteError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profilo") }) },
    ) { padding ->
        when (val state = authState) {
            is AuthUiState.Anonymous ->
                AnonymousProfileContent(
                    modifier = Modifier.padding(padding),
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToRegister = onNavigateToRegister,
                    onDeleteAccount = viewModel::deleteAccount,
                    deleteError = deleteError,
                    onClearDeleteError = viewModel::clearDeleteError,
                )

            is AuthUiState.Authenticated -> {
                val displayUser = user ?: state.user
                AuthenticatedProfileContent(
                    modifier = Modifier.padding(padding),
                    displayName = displayUser.displayName,
                    email = displayUser.email,
                    isAnonymous = false,
                    onSaveDisplayName = viewModel::updateDisplayName,
                    onSignOut = viewModel::signOut,
                    onDeleteAccount = viewModel::deleteAccount,
                    deleteError = deleteError,
                    onClearDeleteError = viewModel::clearDeleteError,
                    onNavigateToRedeemInvitation = onNavigateToRedeemInvitation,
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun AnonymousProfileContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onDeleteAccount: () -> Unit,
    deleteError: String?,
    onClearDeleteError: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    if (deleteError != null) {
        AlertDialog(
            onDismissRequest = onClearDeleteError,
            title = { Text("Errore") },
            text = { Text(deleteError) },
            confirmButton = {
                TextButton(onClick = onClearDeleteError) { Text("OK") }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Stai usando l'app come ospite",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Registrati per sincronizzare i dati e non perderli se cambi dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToRegister) {
            Text("Registrati")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToLogin) {
            Text("Accedi")
        }
        Spacer(Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        TextButton(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Elimina i miei dati")
        }
    }
}

@Composable
private fun AuthenticatedProfileContent(
    modifier: Modifier = Modifier,
    displayName: String,
    email: String?,
    isAnonymous: Boolean,
    onSaveDisplayName: (String) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    deleteError: String?,
    onClearDeleteError: () -> Unit,
    onNavigateToRedeemInvitation: () -> Unit = {},
) {
    var nameInput by remember(displayName) { mutableStateOf(displayName) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    if (deleteError != null) {
        AlertDialog(
            onDismissRequest = onClearDeleteError,
            title = { Text("Errore") },
            text = { Text(deleteError) },
            confirmButton = {
                TextButton(onClick = onClearDeleteError) { Text("OK") }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier =
                Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = displayName.take(1).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Nome visualizzato") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onSaveDisplayName(nameInput) },
            enabled = nameInput.isNotBlank() && nameInput != displayName,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Salva")
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        if (email != null) {
            ProfileInfoRow(label = "Email", value = email)
            Spacer(Modifier.height(8.dp))
        }
        ProfileInfoRow(
            label = "Account",
            value =
                if (isAnonymous) {
                    "Ospite"
                } else if (email != null) {
                    "Email"
                } else {
                    "Google"
                },
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onNavigateToRedeemInvitation,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Riscatta Invito")
        }

        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = onSignOut,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Disconnetti")
        }
        TextButton(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Elimina account")
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elimina account") },
        text = {
            Text(
                "Questa operazione è irreversibile. Tutti i tuoi dati verranno " +
                    "eliminati definitivamente. Sei sicuro di voler continuare?",
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Elimina")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        },
    )
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Profilo — Ospite")
@Composable
private fun PreviewAnonymousProfile() {
    CarezzeTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Profilo") }) },
        ) { padding ->
            AnonymousProfileContent(
                modifier = Modifier.padding(padding),
                onNavigateToLogin = {},
                onNavigateToRegister = {},
                onDeleteAccount = {},
                deleteError = null,
                onClearDeleteError = {},
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Profilo — Autenticato")
@Composable
private fun PreviewAuthenticatedProfile() {
    CarezzeTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Profilo") }) },
        ) { padding ->
            AuthenticatedProfileContent(
                modifier = Modifier.padding(padding),
                displayName = "Mario Rossi",
                email = "mario.rossi@example.com",
                isAnonymous = false,
                onSaveDisplayName = {},
                onSignOut = {},
                onDeleteAccount = {},
                deleteError = null,
                onClearDeleteError = {},
                onNavigateToRedeemInvitation = {},
            )
        }
    }
}
