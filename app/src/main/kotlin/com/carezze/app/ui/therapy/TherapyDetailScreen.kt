package com.fpculcasi.carezze.ui.therapy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapyDetailScreen(
    personId: String,
    therapyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToLog: () -> Unit = {},
    viewModel: TherapyViewModel = hiltViewModel(),
) {
    val therapies by viewModel.therapiesFor(personId).collectAsState()
    val therapy = therapies.firstOrNull { it.id == therapyId }
    val logs by viewModel.logsFor(personId, therapyId).collectAsState()

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showTerminateDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(therapy?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Altre azioni")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Storico dosi") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToLog()
                                },
                            )
                            if (therapy?.isActive == true) {
                                DropdownMenuItem(
                                    text = { Text("Termina") },
                                    onClick = {
                                        showMenu = false
                                        showTerminateDialog = true
                                    },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Elimina") },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                },
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifica terapia")
            }
        },
    ) { padding ->
        if (therapy == null) return@Scaffold
        TherapyDetailContent(
            therapy = therapy,
            progress = viewModel.progressFor(therapy, logs),
            remaining = viewModel.remainingDoses(therapy, logs),
            modifier = Modifier.padding(padding),
        )
    }

    if (showTerminateDialog) {
        AlertDialog(
            onDismissRequest = { showTerminateDialog = false },
            title = { Text("Termina terapia") },
            text = { Text("Vuoi terminare questa terapia? I log esistenti saranno conservati.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTerminateDialog = false
                        viewModel.terminateTherapy(personId, therapyId)
                        onNavigateBack()
                    },
                ) { Text("Termina") }
            },
            dismissButton = {
                TextButton(onClick = { showTerminateDialog = false }) { Text("Annulla") }
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina terapia") },
            text = { Text("Eliminare la terapia rimuoverà anche tutti i log associati. Questa azione è irreversibile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTherapy(personId, therapyId)
                        onNavigateBack()
                    },
                ) { Text("Elimina") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annulla") }
            },
        )
    }
}

@Composable
private fun TherapyDetailContent(
    therapy: Therapy,
    progress: Float,
    remaining: Int,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            if (progress >= 0f) {
                TherapyProgressSection(progress = progress, remaining = remaining)
                Spacer(Modifier.height(8.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Inizio: ${therapy.startDate}", style = MaterialTheme.typography.bodyMedium)
                val durationText =
                    when (val d = therapy.duration) {
                        is TherapyDuration.Indefinite -> "Durata: illimitata"
                        is TherapyDuration.Fixed -> "Durata: ${d.days} giorni"
                    }
                Text(durationText, style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (therapy.isActive) "Stato: attiva" else "Stato: terminata",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (therapy.isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Farmaci", style = MaterialTheme.typography.titleMedium)
        }

        items(therapy.medications) { med ->
            MedicationCard(med = med)
        }
    }
}

@Composable
private fun TherapyProgressSection(
    progress: Float,
    remaining: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Progresso", style = MaterialTheme.typography.titleSmall)
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        if (remaining >= 0) {
            Text(
                "$remaining dosi rimanenti",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MedicationCard(med: Medication) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(med.name, style = MaterialTheme.typography.titleSmall)
            Text(
                "${med.dosage} ${med.dosageUnit} · ogni ${med.frequencyHours}h",
                style = MaterialTheme.typography.bodySmall,
            )
            if (med.scheduledTimes.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    med.scheduledTimes.forEach { time ->
                        Text(time, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            med.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
