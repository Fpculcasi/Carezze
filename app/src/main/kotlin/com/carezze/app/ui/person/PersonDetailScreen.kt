package com.fpculcasi.carezze.ui.person

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import com.fpculcasi.carezze.ui.theme.PersonColorPalette
import com.fpculcasi.carezze.ui.theme.personColor
import com.fpculcasi.carezze.ui.therapy.TherapyViewModel
import java.time.LocalDate

@Composable
fun PersonDetailScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
    onNavigateToTherapy: (personId: String, therapyId: String) -> Unit,
    viewModel: PersonViewModel = hiltViewModel(),
    therapyViewModel: TherapyViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val person = persons.firstOrNull { it.id == personId }
    val therapies by therapyViewModel.therapiesFor(personId).collectAsState()
    val currentColorIndex by viewModel.personColorFlow(personId).collectAsState(initial = 0)

    PersonDetailContent(
        person = person,
        personId = personId,
        therapies = therapies,
        currentColorIndex = currentColorIndex,
        onNavigateBack = onNavigateBack,
        onUpdatePerson = { name, nick ->
            person?.let { viewModel.updatePerson(it.copy(name = name, nickname = nick)) }
        },
        onSetPersonColor = { index -> viewModel.setPersonColor(personId, index) },
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
    currentColorIndex: Int,
    onNavigateBack: () -> Unit,
    onUpdatePerson: (name: String, nickname: String?) -> Unit,
    onSetPersonColor: (Int) -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
    onNavigateToTherapy: (personId: String, therapyId: String) -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editNickname by remember { mutableStateOf("") }

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
                    IconButton(onClick = {
                        editName = person?.name ?: ""
                        editNickname = person?.nickname ?: ""
                        showEditDialog = true
                    }) {
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                ColorPickerRow(
                    currentColorIndex = currentColorIndex,
                    onSelectColor = onSetPersonColor,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (therapies.isEmpty()) {
                item {
                    Text(
                        "Nessuna terapia",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                item {
                    Text(
                        "Terapie",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(therapies) { therapy ->
                    TherapyListItem(
                        therapy = therapy,
                        onClick = { onNavigateToTherapy(personId, therapy.id) },
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifica Persona") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nome *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("Soprannome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdatePerson(editName.trim(), editNickname.trim().takeIf { it.isNotBlank() })
                        showEditDialog = false
                    },
                    enabled = editName.isNotBlank(),
                ) { Text("Salva") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Annulla") }
            },
        )
    }
}

@Composable
private fun ColorPickerRow(
    currentColorIndex: Int,
    onSelectColor: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            "Colore",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PersonColorPalette.forEachIndexed { index, color ->
                ColorSwatch(
                    color = color,
                    selected = index == currentColorIndex,
                    onClick = { onSelectColor(index) },
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color, CircleShape)
            .then(
                if (selected) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick),
    )
}

@Composable
private fun TherapyListItem(
    therapy: Therapy,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(therapy.name, style = MaterialTheme.typography.titleSmall)
                val durationText =
                    when (val d = therapy.duration) {
                        is TherapyDuration.Indefinite -> "Illimitata"
                        is TherapyDuration.Fixed -> "${d.days} giorni"
                    }
                Text(durationText, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                if (therapy.isActive) "Attiva" else "Terminata",
                style = MaterialTheme.typography.labelMedium,
                color =
                    if (therapy.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
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
            therapies =
                listOf(
                    Therapy(
                        id = "t1", personId = "1", name = "Amoxicillina", createdBy = "uid1",
                        startDate = LocalDate.now(), duration = TherapyDuration.Fixed(7),
                        isActive = true, members = emptyMap(), medications = emptyList(),
                    ),
                ),
            currentColorIndex = 2,
            onNavigateBack = {},
            onUpdatePerson = { _, _ -> },
            onSetPersonColor = {},
            onNavigateToAddTherapy = {},
            onNavigateToTherapy = { _, _ -> },
        )
    }
}
