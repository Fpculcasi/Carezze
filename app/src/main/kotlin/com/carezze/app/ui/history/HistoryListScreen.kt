package com.fpculcasi.carezze.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateHeaderFormatter =
    DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN).withZone(ZoneId.systemDefault())
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val logs by viewModel.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storico") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCalendar) {
                        Icon(Icons.Default.DateRange, contentDescription = "Vista calendario")
                    }
                },
            )
        },
    ) { padding ->
        if (logs.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Nessun evento negli ultimi 30 giorni", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            GroupedFeed(logs = logs, contentPadding = padding)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroupedFeed(
    logs: List<ActivityLog>,
    contentPadding: PaddingValues,
) {
    val grouped =
        logs.groupBy { log ->
            log.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
        }.entries.sortedByDescending { it.key }

    LazyColumn(contentPadding = contentPadding) {
        grouped.forEach { (date, dayLogs) ->
            stickyHeader(key = date.toString()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text =
                            dateHeaderFormatter.format(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                .replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(dayLogs, key = { it.id }) { log ->
                Column {
                    ListItem(
                        headlineContent = { Text(log.label()) },
                        supportingContent = { Text(timeFormatter.format(log.timestamp)) },
                        leadingContent = { Text(log.emoji(), style = MaterialTheme.typography.headlineSmall) },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

private fun ActivityLog.label(): String =
    when (this) {
        is ActivityLog.Meal -> mealLabel()
        is ActivityLog.Diaper ->
            "Pannolino · ${
                when (diaperType) {
                    DiaperType.WET -> "Pipì"
                    DiaperType.DIRTY -> "Pupù"
                    DiaperType.BOTH -> "Pipì e pupù"
                    DiaperType.DRY -> "Asciutto"
                }
            }"
        is ActivityLog.SleepStart -> "Inizio sonno"
        is ActivityLog.SleepEnd -> "Fine sonno"
        is ActivityLog.Temperature -> "Temperatura · $temperature°${unit.name}"
        is ActivityLog.Weight -> "Peso · $weight ${weightUnit.name.lowercase()}"
        is ActivityLog.Hygiene -> "Igiene"
    }

private fun ActivityLog.Meal.mealLabel(): String =
    buildString {
        append("Pasto")
        if (mealType != null) append(" · ${mealType.name.lowercase().replaceFirstChar { it.uppercaseChar() }}")
        if (amount != null && amountUnit != null) append(" · $amount ${amountUnit.name.lowercase()}")
    }

private fun ActivityLog.emoji(): String =
    when (this) {
        is ActivityLog.Meal -> "🍼"
        is ActivityLog.Diaper -> "👶"
        is ActivityLog.SleepStart -> "🌙"
        is ActivityLog.SleepEnd -> "☀️"
        is ActivityLog.Temperature -> "🌡️"
        is ActivityLog.Weight -> "⚖️"
        is ActivityLog.Hygiene -> "🛁"
    }
