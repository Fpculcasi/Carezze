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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.MealType
import com.fpculcasi.carezze.domain.model.MealUnit
import com.fpculcasi.carezze.domain.model.TemperatureUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogSheet(
    personId: String,
    onDismiss: () -> Unit,
    viewModel: QuickLogViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

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
                    .navigationBarsPadding(),
        ) {
            Text(
                "Registra evento per $personId",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            if (state.selectedType == null) {
                TypeSelectionGrid(onSelectType = viewModel::selectType)
            } else {
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
private fun TypeForm(
    type: ActivityLogType,
    isLoading: Boolean,
    personId: String,
    viewModel: QuickLogViewModel,
    onBack: () -> Unit,
) {
    var notes by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (type) {
            ActivityLogType.MEAL -> MealForm(personId = personId, isLoading = isLoading, viewModel = viewModel)
            ActivityLogType.DIAPER -> DiaperForm(personId = personId, isLoading = isLoading, viewModel = viewModel)
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
                TemperatureForm(personId = personId, isLoading = isLoading, viewModel = viewModel)
            ActivityLogType.WEIGHT -> WeightForm(personId = personId, isLoading = isLoading, viewModel = viewModel)
            ActivityLogType.HYGIENE -> {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Note (opzionale)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                SaveButton(isLoading = isLoading, onSave = { viewModel.logHygiene(personId, notes) })
            }
        }
        ElevatedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("← Indietro") }
    }
}

@Composable
private fun MealForm(
    personId: String,
    isLoading: Boolean,
    viewModel: QuickLogViewModel,
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
            viewModel.logMeal(personId, amount.toDoubleOrNull(), selectedUnit, selectedType, null)
        })
    }
}

@Composable
private fun DiaperForm(
    personId: String,
    isLoading: Boolean,
    viewModel: QuickLogViewModel,
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
        SaveButton(isLoading = isLoading, onSave = { viewModel.logDiaper(personId, selected, null) })
    }
}

@Composable
private fun TemperatureForm(
    personId: String,
    isLoading: Boolean,
    viewModel: QuickLogViewModel,
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
            temp.toDoubleOrNull()?.let { viewModel.logTemperature(personId, it, unit, null, null) }
        })
    }
}

@Composable
private fun WeightForm(
    personId: String,
    isLoading: Boolean,
    viewModel: QuickLogViewModel,
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
            weight.toDoubleOrNull()?.let {
                viewModel.logWeight(personId, it, com.fpculcasi.carezze.domain.model.WeightUnit.KG, null)
            }
        })
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
                androidx.compose.material3.ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            } else {
                androidx.compose.material3.ButtonDefaults.elevatedButtonColors()
            },
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
