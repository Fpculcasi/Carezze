package com.fpculcasi.carezze.ui.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun PersonListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToPerson: (String) -> Unit,
    viewModel: PersonViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    PersonListContent(
        persons = persons,
        onNavigateBack = onNavigateBack,
        onNavigateToAdd = onNavigateToAdd,
        onNavigateToPerson = onNavigateToPerson,
        onDeletePerson = viewModel::deletePerson,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonListContent(
    persons: List<Person>,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToPerson: (String) -> Unit,
    onDeletePerson: (String) -> Unit,
) {
    var personToDelete by remember { mutableStateOf<Person?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Persone") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Persona")
            }
        },
    ) { padding ->
        if (persons.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Nessuna persona. Premi + per aggiungerne una.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(persons, key = { it.id }) { person ->
                    PersonItem(
                        person = person,
                        onClick = { onNavigateToPerson(person.id) },
                        onDelete = { personToDelete = person },
                    )
                }
            }
        }
    }

    personToDelete?.let { person ->
        AlertDialog(
            onDismissRequest = { personToDelete = null },
            title = { Text("Elimina Persona") },
            text = { Text("Eliminare ${person.name}? L'operazione non è reversibile.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeletePerson(person.id)
                    personToDelete = null
                }) { Text("Elimina") }
            },
            dismissButton = {
                TextButton(onClick = { personToDelete = null }) { Text("Annulla") }
            },
        )
    }
}

@Composable
private fun PersonItem(
    person: Person,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        ListItem(
            headlineContent = { Text(person.name) },
            supportingContent = person.nickname?.let { { Text(it) } },
            trailingContent = {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Elimina")
                }
            },
        )
    }
}

private val previewPersons = listOf(
    Person("1", "Vittoria", "Vicky", "uid1", mapOf("uid1" to MemberRole.OWNER)),
    Person("2", "Christian", null, "uid1", mapOf("uid1" to MemberRole.OWNER)),
)

@Preview(showBackground = true)
@Composable
private fun PersonListContentPreview() {
    CarezzeTheme {
        PersonListContent(
            persons = previewPersons,
            onNavigateBack = {},
            onNavigateToAdd = {},
            onNavigateToPerson = {},
            onDeletePerson = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun PersonListContentEmptyPreview() {
    CarezzeTheme {
        PersonListContent(
            persons = emptyList(),
            onNavigateBack = {},
            onNavigateToAdd = {},
            onNavigateToPerson = {},
            onDeletePerson = {},
        )
    }
}
