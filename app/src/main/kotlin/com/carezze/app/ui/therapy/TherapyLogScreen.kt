package com.fpculcasi.carezze.ui.therapy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapyLogScreen(
    personId: String,
    therapyId: String,
    onNavigateBack: () -> Unit,
    viewModel: TherapyViewModel = hiltViewModel(),
) {
    val therapies by viewModel.therapiesFor(personId).collectAsState()
    val therapy = therapies.firstOrNull { it.id == therapyId }
    val logs by viewModel.logsFor(personId, therapyId).collectAsState()
    val sortedLogs = logs.sortedByDescending { it.scheduledTime }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storico dosi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi dose manuale")
            }
        },
    ) { padding ->
        if (sortedLogs.isEmpty()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Nessun log ancora",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(sortedLogs, key = { it.id }) { log ->
                    MedicationLogCard(
                        log = log,
                        medicationName =
                            therapy?.medications
                                ?.firstOrNull { it.id == log.medicationId }
                                ?.name ?: "Farmaco sconosciuto",
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }

    if (showAddDialog && therapy != null) {
        AddManualLogDialog(
            medications = therapy.medications,
            onDismiss = { showAddDialog = false },
            onConfirm = { medicationId, takenAt ->
                viewModel.addManualLog(personId, therapyId, medicationId, takenAt)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun MedicationLogCard(
    log: MedicationLog,
    medicationName: String,
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(medicationName, style = MaterialTheme.typography.titleSmall)
                Text(
                    formatter.format(log.scheduledTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (log.isManual) {
                    Text(
                        "Manuale",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            StatusBadge(log.status)
        }
    }
}

@Composable
private fun StatusBadge(status: MedicationStatus) {
    val (label, color) =
        when (status) {
            MedicationStatus.TAKEN -> "Presa" to MaterialTheme.colorScheme.primary
            MedicationStatus.SKIPPED -> "Saltata" to MaterialTheme.colorScheme.error
            MedicationStatus.PENDING -> "In attesa" to MaterialTheme.colorScheme.onSurfaceVariant
        }
    Text(label, style = MaterialTheme.typography.labelMedium, color = color)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddManualLogDialog(
    medications: List<Medication>,
    onDismiss: () -> Unit,
    onConfirm: (medicationId: String, takenAt: Instant) -> Unit,
) {
    var selectedMedication by rememberSaveable { mutableStateOf(medications.firstOrNull()?.id ?: "") }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var dateText by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var hourText by rememberSaveable { mutableStateOf(LocalTime.now().hour.toString().padStart(2, '0')) }
    var minuteText by rememberSaveable { mutableStateOf(LocalTime.now().minute.toString().padStart(2, '0')) }

    val selectedMedName = medications.firstOrNull { it.id == selectedMedication }?.name ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi dose manuale") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedMedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Farmaco") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        medications.forEach { med ->
                            DropdownMenuItem(
                                text = { Text(med.name) },
                                onClick = {
                                    selectedMedication = med.id
                                    dropdownExpanded = false
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Data (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { if (it.length <= 2) hourText = it },
                        label = { Text("Ora") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { if (it.length <= 2) minuteText = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }
        },
        confirmButton = {
            val date = runCatching { LocalDate.parse(dateText) }.getOrNull()
            val hour = hourText.toIntOrNull()?.coerceIn(0, 23)
            val minute = minuteText.toIntOrNull()?.coerceIn(0, 59)
            val isValid = selectedMedication.isNotBlank() && date != null && hour != null && minute != null
            TextButton(
                onClick = {
                    if (date != null && hour != null && minute != null) {
                        val instant =
                            date.atTime(hour, minute)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        onConfirm(selectedMedication, instant)
                    }
                },
                enabled = isValid,
            ) { Text("Aggiungi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        },
    )
}
