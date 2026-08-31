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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapyDetailScreen(
    personId: String,
    therapyId: String,
    onNavigateBack: () -> Unit,
    viewModel: TherapyViewModel = hiltViewModel(),
) {
    val therapies by viewModel.therapiesFor(personId).collectAsState()
    val therapy = therapies.firstOrNull { it.id == therapyId }
    val logs by viewModel.logsFor(personId, therapyId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(therapy?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
    ) { padding ->
        if (therapy == null) return@Scaffold
        TherapyDetailContent(
            therapy = therapy,
            logs = logs,
            progress = viewModel.progressFor(therapy, logs),
            remaining = viewModel.remainingDoses(therapy, logs),
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun TherapyDetailContent(
    therapy: Therapy,
    logs: List<MedicationLog>,
    progress: Float,
    remaining: Int,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
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
                val durationText = when (val d = therapy.duration) {
                    is TherapyDuration.Indefinite -> "Durata: illimitata"
                    is TherapyDuration.Fixed -> "Durata: ${d.days} giorni"
                }
                Text(durationText, style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (therapy.isActive) "Stato: attiva" else "Stato: terminata",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (therapy.isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun TherapyProgressSection(progress: Float, remaining: Int) {
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
