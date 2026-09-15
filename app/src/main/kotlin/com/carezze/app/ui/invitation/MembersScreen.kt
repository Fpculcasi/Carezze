package com.fpculcasi.carezze.ui.invitation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.domain.model.MemberRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    viewModel: MembersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var memberToRevoke by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membri — ${uiState.personName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
    ) { padding ->
        if (uiState.members.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nessun membro condiviso.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            ) {
                items(uiState.members, key = { it.userId }) { member ->
                    MemberRow(
                        member = member,
                        isCurrentUser = member.userId == uiState.currentUserId,
                        canRevoke = uiState.isOwner && member.role != MemberRole.OWNER,
                        onRevoke = { memberToRevoke = member.userId },
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }

        memberToRevoke?.let { uid ->
            AlertDialog(
                onDismissRequest = { memberToRevoke = null },
                title = { Text("Revoca accesso") },
                text = { Text("Vuoi revocare l'accesso a questo membro? I suoi dati inseriti verranno eliminati.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.revokeAccess(uid)
                            memberToRevoke = null
                        },
                    ) { Text("Revoca", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { memberToRevoke = null }) { Text("Annulla") }
                },
            )
        }
    }
}

@Composable
private fun MemberRow(
    member: MemberItem,
    isCurrentUser: Boolean,
    canRevoke: Boolean,
    onRevoke: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.userId + if (isCurrentUser) " (tu)" else "",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = if (member.role == MemberRole.OWNER) "Proprietario" else "Editor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (canRevoke) {
                Spacer(modifier = Modifier.padding(8.dp))
                TextButton(onClick = onRevoke) {
                    Text("Revoca", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
