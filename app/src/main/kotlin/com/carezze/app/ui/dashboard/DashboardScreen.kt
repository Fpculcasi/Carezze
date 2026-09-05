package com.fpculcasi.carezze.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
private val dateFormatter = DateTimeFormatter.ofPattern("d MMM").withZone(ZoneId.systemDefault())

@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPersons: () -> Unit = {},
    onNavigateToHistory: (personId: String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    var showQuickLog by remember { mutableStateOf(false) }

    DashboardContent(
        persons = persons,
        recentLogs = recentLogs,
        selectedPersonId = selectedPersonId,
        viewMode = viewMode,
        onSelectPerson = viewModel::selectPerson,
        onToggleViewMode = viewModel::toggleViewMode,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToPersons = onNavigateToPersons,
        onNavigateToHistory = onNavigateToHistory,
        onOpenQuickLog = { showQuickLog = true },
    )

    if (showQuickLog) {
        QuickLogSheet(
            personId = selectedPersonId ?: persons.firstOrNull()?.id ?: "",
            onDismiss = { showQuickLog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardContent(
    persons: List<Person>,
    recentLogs: List<ActivityLog>,
    selectedPersonId: String?,
    viewMode: DashboardViewMode,
    onSelectPerson: (String?) -> Unit,
    onToggleViewMode: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPersons: () -> Unit,
    onNavigateToHistory: (personId: String) -> Unit,
    onOpenQuickLog: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carezze", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onToggleViewMode) {
                        Icon(
                            if (viewMode == DashboardViewMode.CARD) Icons.Default.DateRange else Icons.Default.Person,
                            contentDescription = if (viewMode == DashboardViewMode.CARD) "Vista feed" else "Vista card",
                        )
                    }
                    IconButton(onClick = onNavigateToPersons) {
                        Icon(Icons.Default.Person, contentDescription = "Gestisci persone")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenQuickLog) {
                Icon(Icons.Default.Add, contentDescription = "Registra evento")
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            PersonFilterRow(
                persons = persons,
                selectedPersonId = selectedPersonId,
                onSelectPerson = onSelectPerson,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
            if (viewMode == DashboardViewMode.CARD) {
                CardView(
                    persons = persons,
                    recentLogs = recentLogs,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToPersons = onNavigateToPersons,
                )
            } else {
                FeedView(
                    logs = recentLogs,
                    persons = persons,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonFilterRow(
    persons: List<Person>,
    selectedPersonId: String?,
    onSelectPerson: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        item {
            FilterChip(
                selected = selectedPersonId == null,
                onClick = { onSelectPerson(null) },
                label = { Text("Tutti") },
            )
        }
        items(persons) { person ->
            FilterChip(
                selected = selectedPersonId == person.id,
                onClick = { onSelectPerson(person.id) },
                label = { Text(person.nickname ?: person.name) },
            )
        }
    }
}

@Composable
private fun CardView(
    persons: List<Person>,
    recentLogs: List<ActivityLog>,
    onNavigateToHistory: (personId: String) -> Unit,
    onNavigateToPersons: () -> Unit,
) {
    if (persons.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nessuna persona ancora", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Aggiungi una persona per iniziare",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onNavigateToPersons) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Gestisci persone")
                }
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(persons) { person ->
                val logCount = recentLogs.count { it.personId == person.id }
                PersonCard(
                    person = person,
                    recentLogCount = logCount,
                    onNavigateToHistory = { onNavigateToHistory(person.id) },
                )
            }
        }
    }
}

@Composable
private fun PersonCard(
    person: Person,
    recentLogCount: Int,
    onNavigateToHistory: () -> Unit,
) {
    ElevatedCard(
        onClick = onNavigateToHistory,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgedBox(
                badge = {
                    if (recentLogCount > 0) {
                        Badge { Text(recentLogCount.toString()) }
                    }
                },
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.size(16.dp))
            Column {
                Text(person.name, style = MaterialTheme.typography.titleMedium)
                if (person.nickname != null) {
                    Text(
                        person.nickname,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    if (recentLogCount == 0) {
                        "Nessun evento negli ultimi 7 giorni"
                    } else {
                        "$recentLogCount eventi negli ultimi 7 giorni"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FeedView(
    logs: List<ActivityLog>,
    persons: List<Person>,
) {
    if (logs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Nessun evento recente",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        val personMap = persons.associateBy { it.id }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(logs) { log ->
                ActivityLogFeedItem(
                    log = log,
                    personName = personMap[log.personId]?.nickname ?: personMap[log.personId]?.name,
                )
            }
        }
    }
}

@Composable
private fun ActivityLogFeedItem(
    log: ActivityLog,
    personName: String?,
) {
    ListItem(
        headlineContent = { Text(log.label()) },
        supportingContent = {
            val personPart = if (personName != null) "$personName · " else ""
            Text("$personPart${timeFormatter.format(log.timestamp)} ${dateFormatter.format(log.timestamp)}")
        },
        leadingContent = {
            Text(
                log.emoji(),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
    )
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

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview() {
    val person = Person("p1", "Vittoria", "Vicky", "uid1", emptyMap())
    val log = ActivityLog.Diaper("l1", "p1", Instant.now(), "uid1", DiaperType.WET, null)
    CarezzeTheme {
        DashboardContent(
            persons = listOf(person),
            recentLogs = listOf(log),
            selectedPersonId = null,
            viewMode = DashboardViewMode.CARD,
            onSelectPerson = {},
            onToggleViewMode = {},
            onNavigateToSettings = {},
            onNavigateToPersons = {},
            onNavigateToHistory = {},
            onOpenQuickLog = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview2() {
    val person = Person("p1", "Vittoria", "Vicky", "uid1", emptyMap())
    val log = ActivityLog.Diaper("l1", "p1", Instant.now(), "uid1", DiaperType.WET, null)
    CarezzeTheme {
        DashboardContent(
            persons = listOf(person),
            recentLogs = listOf(log),
            selectedPersonId = "p1",
            viewMode = DashboardViewMode.CARD,
            onSelectPerson = {},
            onToggleViewMode = {},
            onNavigateToSettings = {},
            onNavigateToPersons = {},
            onNavigateToHistory = {},
            onOpenQuickLog = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreviewQuickLog() {
    val person = Person("p1", "Vittoria", "Vicky", "uid1", emptyMap())
    val log = ActivityLog.Diaper("l1", "p1", Instant.now(), "uid1", DiaperType.WET, null)
    CarezzeTheme {
        DashboardContent(
            persons = listOf(person),
            recentLogs = listOf(log),
            selectedPersonId = "p1",
            viewMode = DashboardViewMode.CARD,
            onSelectPerson = {},
            onToggleViewMode = {},
            onNavigateToSettings = {},
            onNavigateToPersons = {},
            onNavigateToHistory = {},
            onOpenQuickLog = {},
        )
    }
}
