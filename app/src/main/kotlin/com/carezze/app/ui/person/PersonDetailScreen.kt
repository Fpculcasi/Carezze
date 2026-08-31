package com.fpculcasi.carezze.ui.person

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun PersonDetailScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: PersonViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val person = persons.firstOrNull { it.id == personId }
    PersonDetailContent(
        person = person,
        personId = personId,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = onNavigateToEdit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonDetailContent(
    person: Person?,
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(personId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifica")
                    }
                },
            )
        },
    ) { padding ->
        // TODO(M4/M5): terapie, log attività
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Terapie e log attività — M4/M5",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonDetailContentPreview() {
    CarezzeTheme {
        PersonDetailContent(
            person = Person("1", "Sofia", "Sofi", "uid1", mapOf("uid1" to MemberRole.OWNER)),
            personId = "1",
            onNavigateBack = {},
            onNavigateToEdit = {},
        )
    }
}
