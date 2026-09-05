package com.fpculcasi.carezze.ui.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.ui.therapy.TherapyViewModel
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import java.time.LocalDate

@Composable
fun PersonDetailScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
    onNavigateToTherapy: (personId: String, therapyId: String) -> Unit,
    viewModel: PersonViewModel = hiltViewModel(),
    therapyViewModel: TherapyViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val person = persons.firstOrNull { it.id == personId }
    val therapies by therapyViewModel.therapiesFor(personId).collectAsState()

    PersonDetailContent(
        person = person,
        personId = personId,
        therapies = therapies,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = onNavigateToEdit,
        onNavigateToAddTherapy = onNavigateToAddTherapy,
        onNavigateToTherapy = onNavigateToTherapy,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonDetailContent(
    person: Person?,
    personId: String,
    therapies: List<Therapy>,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
    onNavigateToTherapy: (personId: String, therapyId: String) -> Unit,
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
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddTherapy(personId) }) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi terapia")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (therapies.isEmpty()) {
                item {
                    Text(
                        "Nessuna terapia",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            } else {
                item { Text("Terapie", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                items(therapies) { therapy ->
                    TherapyListItem(
                        therapy = therapy,
                        onClick = { onNavigateToTherapy(personId, therapy.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TherapyListItem(therapy: Therapy, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(therapy.name, style = MaterialTheme.typography.titleSmall)
                val durationText = when (val d = therapy.duration) {
                    is TherapyDuration.Indefinite -> "Illimitata"
                    is TherapyDuration.Fixed -> "${d.days} giorni"
                }
                Text(durationText, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                if (therapy.isActive) "Attiva" else "Terminata",
                style = MaterialTheme.typography.labelMedium,
                color = if (therapy.isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonDetailContentPreview() {
    CarezzeTheme {
        PersonDetailContent(
            person = Person("1", "Vittoria", "Vicky", "uid1", mapOf("uid1" to MemberRole.OWNER)),
            personId = "1",
            therapies = listOf(
                Therapy(
                    id = "t1", personId = "1", name = "Amoxicillina", createdBy = "uid1",
                    startDate = LocalDate.now(), duration = TherapyDuration.Fixed(7),
                    isActive = true, members = emptyMap(), medications = emptyList(),
                ),
            ),
            onNavigateBack = {},
            onNavigateToEdit = {},
            onNavigateToAddTherapy = {},
            onNavigateToTherapy = { _, _ -> },
        )
    }
}
