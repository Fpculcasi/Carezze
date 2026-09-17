package com.fpculcasi.carezze.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

private const val PRIVACY_POLICY_URL = "https://fpculcasi.github.io/carezze/privacy"

@Composable
fun PrivacyDataScreen(onNavigateBack: () -> Unit) {
    PrivacyDataContent(onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PrivacyDataContent(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Dati") },
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
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cosa raccogliamo",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Carezze raccoglie i dati che inserisci direttamente nell'app:",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "Profili delle persone a cui presti cura",
                "Log di attività (pasti, pannolini, sonno, temperatura, peso, igiene)",
                "Terapie e dosi di farmaci",
                "Indirizzo email e impostazioni account (se registrato)",
                "Token FCM per le notifiche push",
            ).forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Come usiamo i tuoi dati",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "I dati vengono usati esclusivamente per il funzionamento dell'app. " +
                    "Non vendiamo né condividiamo i tuoi dati con terze parti a fini commerciali. " +
                    "I dati sono archiviati su Firebase (Google) in conformità al GDPR.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "I tuoi diritti",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hai il diritto di accedere, modificare ed eliminare i tuoi dati in qualsiasi momento. " +
                    "Puoi eliminare il tuo account dalla schermata Profilo (Art. 17 GDPR).",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                    )
                },
            ) {
                Text("Leggi la policy completa")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyDataContentPreview() {
    CarezzeTheme {
        PrivacyDataContent(onNavigateBack = {})
    }
}
