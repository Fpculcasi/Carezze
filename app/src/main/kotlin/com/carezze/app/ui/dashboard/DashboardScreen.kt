package com.fpculcasi.carezze.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPersons: () -> Unit = {},
) {
    DashboardContent(
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToPersons = onNavigateToPersons,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardContent(
    onNavigateToSettings: () -> Unit,
    onNavigateToPersons: () -> Unit,
) {
    // TODO(M5): implement full dashboard — card view per Persona + feed cronologico
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedButton(
                onClick = onNavigateToPersons,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Gestisci Persone")
            }
            Spacer(Modifier.height(16.dp))
            Text("Dashboard completa — M5", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview() {
    CarezzeTheme {
        DashboardContent(
            onNavigateToSettings = {},
            onNavigateToPersons = {},
        )
    }
}
