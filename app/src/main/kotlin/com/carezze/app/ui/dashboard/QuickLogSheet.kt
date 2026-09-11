package com.fpculcasi.carezze.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.MealType
import com.fpculcasi.carezze.domain.model.MealUnit
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.model.WeightUnit
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogSheet(
    personId: String,
    personName: String,
    onDismiss: () -> Unit,
    onNavigateToAddTherapy: (personId: String) -> Unit = {},
    onNavigateToTherapyLog: (personId: String, therapyId: String) -> Unit = { _, _ -> },
    viewModel: QuickLogViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    LaunchedEffect(Unit) { viewModel.clearType() }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "Registra evento per $personName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            when {
                state.selectedType == null ->
                    TypeSelectionGrid(
                        onSelectType = { type ->
                            viewModel.selectType(
                                type,
                                if (type == ActivityLogType.THERAPY) personId else null,
                            )
                        },
                    )
                state.selectedType == ActivityLogType.THERAPY ->
                    TherapyStepContent(
                        state = state,
                        personId = personId,
                        onSelectTherapy = viewModel::selectTherapy,
                        onClearTherapy = viewModel::clearTherapy,
                        onSelectMedication = viewModel::selectMedication,
                        onClearMedication = viewModel::clearMedication,
                        onLogMedication = {
                            viewModel.logMedication(
                                personId,
                                state.selectedTherapyId!!,
                                state.selectedMedicationId!!,
                            )
                        },
                        onConfirmScheduledDose = { dose -> viewModel.confirmScheduledDose(personId, dose) },
                        onEnterManualDoseFlow = viewModel::enterManualDoseFlow,
                        onExitManualDoseFlow = viewModel::exitManualDoseFlow,
                        onBack = viewModel::clearType,
                        onNavigateToAddTherapy = onNavigateToAddTherapy,
                        onNavigateToTherapyLog = onNavigateToTherapyLog,
                    )
                else ->
                    TypeForm(
                        type = state.selectedType!!,
                        isLoading = state.isLoading,
                        personId = personId,
                        viewModel = viewModel,
                        onBack = viewModel::clearType,
                    )
            }

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TypeSelectionGrid(onSelectType: (ActivityLogType) -> Unit) {
    val types =
        listOf(
            ActivityLogType.MEAL to "🍼 Pasto",
            ActivityLogType.DIAPER to "👶 Pannolino",
            ActivityLogType.SLEEP_START to "🌙 Inizio sonno",
            ActivityLogType.SLEEP_END to "☀️ Fine sonno",
            ActivityLogType.TEMPERATURE to "🌡️ Temperatura",
            ActivityLogType.WEIGHT to "⚖️ Peso",
            ActivityLogType.HYGIENE to "🛁 Igiene",
            ActivityLogType.THERAPY to "💊 Farmaci",
        )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        types.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (type, label) ->
                    ElevatedButton(
                        onClick = { onSelectType(type) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TherapyStepContent(
    state: QuickLogUiState,
    personId: String,
    onSelectTherapy: (String) -> Unit,
    onClearTherapy: () -> Unit,
    onSelectMedication: (String) -> Unit,
    onClearMedication: () -> Unit,
    onLogMedication: () -> Unit,
    onConfirmScheduledDose: (ScheduledDose) -> Unit,
    onEnterManualDoseFlow: () -> Unit,
    onExitManualDoseFlow: () -> Unit,
    onBack: () -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
    onNavigateToTherapyLog: (String, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.showManualDoseFlow) {
            ManualDoseFlowContent(
                state = state,
                personId = personId,
                onSelectTherapy = onSelectTherapy,
                onClearTherapy = onClearTherapy,
                onSelectMedication = onSelectMedication,
                onClearMedication = onClearMedication,
                onLogMedication = onLogMedication,
                onExitManualDoseFlow = onExitManualDoseFlow,
                onNavigateToTherapyLog = onNavigateToTherapyLog,
            )
        } else {
            ScheduledDosesContent(
                state = state,
                personId = personId,
                onConfirmScheduledDose = onConfirmScheduledDose,
                onEnterManualDoseFlow = onEnterManualDoseFlow,
                onNavigateToAddTherapy = onNavigateToAddTherapy,
            )
        }
        ElevatedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("← Indietro") }
    }
}

@Composable
private fun ScheduledDosesContent(
    state: QuickLogUiState,
    personId: String,
    onConfirmScheduledDose: (ScheduledDose) -> Unit,
    onEnterManualDoseFlow: () -> Unit,
    onNavigateToAddTherapy: (String) -> Unit,
) {
    Text(
        "Dosi di oggi",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    when {
        state.therapies.isEmpty() -> {
            Text(
                "Nessuna terapia attiva",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { onNavigateToAddTherapy(personId) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Aggiungi terapia") }
        }
        state.scheduledDoses.isEmpty() -> {
            Text(
                "Nessuna dose pianificata per oggi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = onEnterManualDoseFlow,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("+ Aggiungi dose extra") }
        }
        else -> {
            state.scheduledDoses.groupBy { it.therapyId }.forEach { (_, doses) ->
                Text(
                    doses.first().therapyName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                doses.forEach { dose ->
                    DoseRow(
                        dose = dose,
                        isLoading = state.isLoading,
                        onConfirm = { onConfirmScheduledDose(dose) },
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = onEnterManualDoseFlow,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("+ Aggiungi dose extra") }
        }
    }
}

@Composable
private fun ManualDoseFlowContent(
    state: QuickLogUiState,
    personId: String,
    onSelectTherapy: (String) -> Unit,
    onClearTherapy: () -> Unit,
    onSelectMedication: (String) -> Unit,
    onClearMedication: () -> Unit,
    onLogMedication: () -> Unit,
    onExitManualDoseFlow: () -> Unit,
    onNavigateToTherapyLog: (String, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(
            onClick = onExitManualDoseFlow,
            modifier = Modifier.align(Alignment.Start),
        ) { Text("← Dosi di oggi") }
        when {
            state.selectedTherapyId == null -> {
                Text(
                    "Seleziona terapia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                state.therapies.forEach { therapy ->
                    ElevatedCard(
                        onClick = { onSelectTherapy(therapy.id) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                therapy.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                "${therapy.medications.size} farmaco/i",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            state.selectedMedicationId == null -> {
                val therapy = state.therapies.find { it.id == state.selectedTherapyId }
                TextButton(
                    onClick = onClearTherapy,
                    modifier = Modifier.align(Alignment.Start),
                ) { Text("← ${therapy?.name ?: "Terapia"}") }
                Text(
                    "Seleziona farmaco",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                therapy?.medications?.forEach { med ->
                    ElevatedCard(
                        onClick = { onSelectMedication(med.id) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                med.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                "${med.dosage} ${med.dosageUnit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            else -> {
                val therapy = state.therapies.find { it.id == state.selectedTherapyId }
                val med = therapy?.medications?.find { it.id == state.selectedMedicationId }
                TextButton(
                    onClick = onClearMedication,
                    modifier = Modifier.align(Alignment.Start),
                ) { Text("← ${med?.name ?: "Farmaco"}") }
                Text(
                    med?.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${med?.dosage} ${med?.dosageUnit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onLogMedication,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Segna presa")
                }
                TextButton(
                    onClick = { onNavigateToTherapyLog(personId, state.selectedTherapyId!!) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Vai allo storico →") }
            }
        }
    }
}

@Composable
private fun DoseRow(
    dose: ScheduledDose,
    isLoading: Boolean,
    onConfirm: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dose.medicationName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    dose.timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onConfirm, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Segna")
                }
            }
        }
    }
}

@Composable
private fun TypeForm(
    type: ActivityLogType,
    isLoading: Boolean,
    personId: String,
    viewModel: QuickLogViewModel,
    onBack: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (type) {
            ActivityLogType.MEAL ->
                MealForm(
                    isLoading = isLoading,
                    onSave = { amount, unit, mealType, notes ->
                        viewModel.logMeal(personId, amount, unit, mealType, notes)
                    },
                )
            ActivityLogType.DIAPER ->
                DiaperForm(
                    isLoading = isLoading,
                    onSave = { diaperType, notes -> viewModel.logDiaper(personId, diaperType, notes) },
                )
            ActivityLogType.SLEEP_START ->
                QuickSaveForm(
                    label = "Inizio sonno registrato",
                    isLoading = isLoading,
                    onSave = { viewModel.logSleep(personId, isStart = true) },
                )
            ActivityLogType.SLEEP_END ->
                QuickSaveForm(
                    label = "Fine sonno registrata",
                    isLoading = isLoading,
                    onSave = { viewModel.logSleep(personId, isStart = false) },
                )
            ActivityLogType.TEMPERATURE ->
                TemperatureForm(
                    isLoading = isLoading,
                    onSave = { temp, unit -> viewModel.logTemperature(personId, temp, unit, null, null) },
                )
            ActivityLogType.WEIGHT ->
                WeightForm(
                    isLoading = isLoading,
                    onSave = { weight -> viewModel.logWeight(personId, weight, WeightUnit.KG, null) },
                )
            ActivityLogType.HYGIENE ->
                HygieneForm(
                    isLoading = isLoading,
                    onSave = { notes -> viewModel.logHygiene(personId, notes) },
                )
            ActivityLogType.THERAPY -> {}
        }
        ElevatedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("← Indietro") }
    }
}

@Composable
private fun MealForm(
    isLoading: Boolean,
    onSave: (Double?, MealUnit, MealType, String?) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf(MealUnit.ML) }
    var selectedType by remember { mutableStateOf(MealType.FORMULA) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MealType.entries.forEach { t ->
                FilterToggle(
                    label =
                        when (t) {
                            MealType.BREAST -> "Seno"
                            MealType.FORMULA -> "Formula"
                            MealType.SOLID -> "Solido"
                        },
                    selected = selectedType == t,
                    onClick = { selectedType = t },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Quantità") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            MealUnit.entries.forEach { u ->
                FilterToggle(
                    label = u.name.lowercase(),
                    selected = selectedUnit == u,
                    onClick = { selectedUnit = u },
                )
            }
        }
        SaveButton(isLoading = isLoading, onSave = {
            onSave(amount.toDoubleOrNull(), selectedUnit, selectedType, null)
        })
    }
}

@Composable
private fun DiaperForm(
    isLoading: Boolean,
    onSave: (DiaperType, String?) -> Unit,
) {
    var selected by remember { mutableStateOf(DiaperType.WET) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                DiaperType.WET to "Pipì",
                DiaperType.DIRTY to "Pupù",
                DiaperType.BOTH to "Entrambi",
                DiaperType.DRY to "Asciutto",
            ).forEach {
                    (t, label) ->
                FilterToggle(
                    label = label,
                    selected = selected == t,
                    onClick = { selected = t },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        SaveButton(isLoading = isLoading, onSave = { onSave(selected, null) })
    }
}

@Composable
private fun TemperatureForm(
    isLoading: Boolean,
    onSave: (Double, TemperatureUnit) -> Unit,
) {
    var temp by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(TemperatureUnit.C) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = temp,
                onValueChange = { temp = it },
                label = { Text("Temperatura") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
            TemperatureUnit.entries.forEach { u ->
                FilterToggle(label = "°${u.name}", selected = unit == u, onClick = { unit = u })
            }
        }
        SaveButton(isLoading = isLoading, onSave = {
            temp.toDoubleOrNull()?.let { onSave(it, unit) }
        })
    }
}

@Composable
private fun WeightForm(
    isLoading: Boolean,
    onSave: (Double) -> Unit,
) {
    var weight by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        SaveButton(isLoading = isLoading, onSave = {
            weight.toDoubleOrNull()?.let { onSave(it) }
        })
    }
}

@Composable
private fun HygieneForm(
    isLoading: Boolean,
    onSave: (String?) -> Unit,
) {
    var notes by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Note (opzionale)") },
            modifier = Modifier.fillMaxWidth(),
        )
        SaveButton(isLoading = isLoading, onSave = { onSave(notes.ifBlank { null }) })
    }
}

@Composable
private fun QuickSaveForm(
    label: String,
    isLoading: Boolean,
    onSave: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        SaveButton(isLoading = isLoading, onSave = onSave)
    }
}

@Composable
private fun SaveButton(
    isLoading: Boolean,
    onSave: () -> Unit,
) {
    Button(
        onClick = onSave,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.width(8.dp))
        } else {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text("Salva")
    }
}

@Composable
private fun FilterToggle(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        colors =
            if (selected) {
                ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            } else {
                ButtonDefaults.elevatedButtonColors()
            },
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

// region Previews

private val previewMedication =
    Medication(
        id = "m1",
        name = "Paracetamolo",
        dosage = 5.0,
        dosageUnit = "ml",
        frequencyHours = 8,
        scheduledTimes = emptyList(),
        startDate = LocalDate.now(),
        notes = null,
    )

private val previewTherapy =
    Therapy(
        id = "t1",
        personId = "p1",
        name = "Febbre",
        createdBy = "u1",
        startDate = LocalDate.now(),
        duration = TherapyDuration.Indefinite,
        isActive = true,
        members = emptyMap(),
        medications = listOf(previewMedication),
    )

@Preview(showBackground = true, name = "Selezione tipo")
@Composable
private fun PreviewTypeSelectionGrid() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TypeSelectionGrid(onSelectType = {})
        }
    }
}

@Preview(showBackground = true, name = "Terapie — nessuna attiva")
@Composable
private fun PreviewTherapyEmpty() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TherapyStepContent(
                state = QuickLogUiState(therapies = emptyList()),
                personId = "p1",
                onSelectTherapy = {},
                onClearTherapy = {},
                onSelectMedication = {},
                onClearMedication = {},
                onLogMedication = {},
                onConfirmScheduledDose = {},
                onEnterManualDoseFlow = {},
                onExitManualDoseFlow = {},
                onBack = {},
                onNavigateToAddTherapy = {},
                onNavigateToTherapyLog = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Terapie — dosi schedulate")
@Composable
private fun PreviewTherapyScheduledDoses() {
    val dose =
        ScheduledDose(
            therapyId = "t1",
            therapyName = "Febbre",
            medicationId = "m1",
            medicationName = "Paracetamolo",
            timeLabel = "08:00",
            scheduledTime = java.time.Instant.now(),
        )
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TherapyStepContent(
                state = QuickLogUiState(therapies = listOf(previewTherapy), scheduledDoses = listOf(dose)),
                personId = "p1",
                onSelectTherapy = {},
                onClearTherapy = {},
                onSelectMedication = {},
                onClearMedication = {},
                onLogMedication = {},
                onConfirmScheduledDose = {},
                onEnterManualDoseFlow = {},
                onExitManualDoseFlow = {},
                onBack = {},
                onNavigateToAddTherapy = {},
                onNavigateToTherapyLog = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Terapie — flusso manuale selezione farmaco")
@Composable
private fun PreviewTherapyManualMedicationPicker() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TherapyStepContent(
                state =
                    QuickLogUiState(
                        therapies = listOf(previewTherapy),
                        showManualDoseFlow = true,
                        selectedTherapyId = "t1",
                    ),
                personId = "p1",
                onSelectTherapy = {},
                onClearTherapy = {},
                onSelectMedication = {},
                onClearMedication = {},
                onLogMedication = {},
                onConfirmScheduledDose = {},
                onEnterManualDoseFlow = {},
                onExitManualDoseFlow = {},
                onBack = {},
                onNavigateToAddTherapy = {},
                onNavigateToTherapyLog = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Terapie — flusso manuale conferma dose")
@Composable
private fun PreviewTherapyManualConfirmDose() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TherapyStepContent(
                state =
                    QuickLogUiState(
                        therapies = listOf(previewTherapy),
                        showManualDoseFlow = true,
                        selectedTherapyId = "t1",
                        selectedMedicationId = "m1",
                    ),
                personId = "p1",
                onSelectTherapy = {},
                onClearTherapy = {},
                onSelectMedication = {},
                onClearMedication = {},
                onLogMedication = {},
                onConfirmScheduledDose = {},
                onEnterManualDoseFlow = {},
                onExitManualDoseFlow = {},
                onBack = {},
                onNavigateToAddTherapy = {},
                onNavigateToTherapyLog = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Form pasto")
@Composable
private fun PreviewMealForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            MealForm(isLoading = false, onSave = { _, _, _, _ -> })
        }
    }
}

@Preview(showBackground = true, name = "Form pannolino")
@Composable
private fun PreviewDiaperForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            DiaperForm(isLoading = false, onSave = { _, _ -> })
        }
    }
}

@Preview(showBackground = true, name = "Form sonno")
@Composable
private fun PreviewSleepForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            QuickSaveForm(label = "Inizio sonno registrato", isLoading = false, onSave = {})
        }
    }
}

@Preview(showBackground = true, name = "Form temperatura")
@Composable
private fun PreviewTemperatureForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            TemperatureForm(isLoading = false, onSave = { _, _ -> })
        }
    }
}

@Preview(showBackground = true, name = "Form peso")
@Composable
private fun PreviewWeightForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            WeightForm(isLoading = false, onSave = {})
        }
    }
}

@Preview(showBackground = true, name = "Form igiene")
@Composable
private fun PreviewHygieneForm() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            HygieneForm(isLoading = false, onSave = {})
        }
    }
}

@Preview(showBackground = true, name = "Pulsante salva")
@Composable
private fun PreviewSaveButton() {
    CarezzeTheme {
        Column(Modifier.padding(16.dp)) {
            SaveButton(isLoading = false, onSave = {})
        }
    }
}

@Preview(showBackground = true, name = "FilterToggle selezionato")
@Composable
private fun PreviewFilterToggleSelected() {
    CarezzeTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterToggle(label = "Seno", selected = true, onClick = {})
            FilterToggle(label = "Formula", selected = false, onClick = {})
        }
    }
}

// endregion
