package com.fpculcasi.carezze.ui.therapy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTherapyScreen(
    personId: String,
    onNavigateBack: () -> Unit,
    viewModel: TherapyViewModel = hiltViewModel(),
) {
    val form by viewModel.form.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (form.step == 1) "Nuova terapia" else "Farmaci") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (form.step > 1) {
                            viewModel.prevStep()
                        } else {
                            viewModel.resetForm()
                            onNavigateBack()
                        }
                    }) {
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
            LinearProgressIndicator(
                progress = { form.step / 2f },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            when (form.step) {
                1 -> StepOneContent(form, viewModel)
                2 -> StepTwoContent(form, viewModel, personId, onNavigateBack)
            }
        }
    }
}

@Composable
private fun StepOneContent(
    form: AddTherapyFormState,
    viewModel: TherapyViewModel,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = form.therapyName,
            onValueChange = viewModel::updateTherapyName,
            label = { Text("Nome terapia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = form.startDate.toString(),
            onValueChange = {
                runCatching { java.time.LocalDate.parse(it) }.getOrNull()?.let(viewModel::updateStartDate)
            },
            label = { Text("Data inizio (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = form.isFixed, onCheckedChange = viewModel::updateIsFixed)
            Spacer(Modifier.width(8.dp))
            Text("Durata fissa")
        }

        if (form.isFixed) {
            OutlinedTextField(
                value = form.fixedDays,
                onValueChange = viewModel::updateFixedDays,
                label = { Text("Giorni") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = viewModel::nextStep,
            modifier = Modifier.fillMaxWidth(),
            enabled = form.therapyName.isNotBlank(),
        ) {
            Text("Avanti")
        }
    }
}

@Composable
private fun StepTwoContent(
    form: AddTherapyFormState,
    viewModel: TherapyViewModel,
    personId: String,
    onDone: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        form.medications.forEachIndexed { index, med ->
            MedicationFormItem(
                med = med,
                canDelete = form.medications.size > 1,
                onUpdate = viewModel::updateMedication,
                onDelete = { viewModel.removeMedication(med.id) },
            )
            if (index < form.medications.lastIndex) HorizontalDivider()
        }

        TextButton(
            onClick = viewModel::addMedication,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Aggiungi farmaco")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.submitTherapy(personId, onDone) },
            modifier = Modifier.fillMaxWidth(),
            enabled = form.medications.any { it.name.isNotBlank() },
        ) {
            Text("Crea terapia")
        }
    }
}

@Composable
private fun MedicationFormItem(
    med: MedicationFormState,
    canDelete: Boolean,
    onUpdate: (MedicationFormState) -> Unit,
    onDelete: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Farmaco", style = MaterialTheme.typography.titleSmall)
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Rimuovi")
                }
            }
        }
        OutlinedTextField(
            value = med.name,
            onValueChange = { onUpdate(med.copy(name = it)) },
            label = { Text("Nome farmaco") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = med.dosage,
                onValueChange = { onUpdate(med.copy(dosage = it)) },
                label = { Text("Dose") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            OutlinedTextField(
                value = med.dosageUnit,
                onValueChange = { onUpdate(med.copy(dosageUnit = it)) },
                label = { Text("Unità") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }
        OutlinedTextField(
            value = med.frequencyHours.toString(),
            onValueChange = { onUpdate(med.copy(frequencyHours = it.toIntOrNull() ?: med.frequencyHours)) },
            label = { Text("Ogni quante ore") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}
