package com.fpculcasi.carezze.ui.person

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun EditPersonScreen(
    personId: String?,
    onNavigateBack: () -> Unit,
    viewModel: PersonViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val existing = remember(personId, persons) { persons.firstOrNull { it.id == personId } }

    EditPersonContent(
        existing = existing,
        onSave = { name, nickname ->
            if (existing != null) {
                viewModel.updatePerson(existing.copy(name = name, nickname = nickname))
            } else {
                viewModel.createPerson(name, nickname)
            }
            onNavigateBack()
        },
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditPersonContent(
    existing: Person?,
    onSave: (name: String, nickname: String?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val isEdit = existing != null
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name ?: "") }
    var nickname by rememberSaveable(existing?.id) { mutableStateOf(existing?.nickname ?: "") }

    LaunchedEffect(existing) {
        if (existing != null && name.isEmpty()) {
            name = existing.name
            nickname = existing.nickname ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Modifica Persona" else "Nuova Persona") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Soprannome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onSave(name.trim(), nickname.trim().takeIf { it.isNotBlank() }) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isEdit) "Salva" else "Crea")
            }
        }
    }
}

@Preview(showBackground = true, name = "Create")
@Composable
private fun EditPersonContentCreatePreview() {
    CarezzeTheme {
        EditPersonContent(
            existing = null,
            onSave = { _, _ -> },
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Edit")
@Composable
private fun EditPersonContentEditPreview() {
    CarezzeTheme {
        EditPersonContent(
            existing = Person("1", "Vittoria", "Vicky", "uid1", mapOf("uid1" to MemberRole.OWNER)),
            onSave = { _, _ -> },
            onNavigateBack = {},
        )
    }
}
