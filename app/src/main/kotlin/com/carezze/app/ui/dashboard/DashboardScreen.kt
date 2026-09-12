package com.fpculcasi.carezze.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.ui.theme.CarezzeTheme
import com.fpculcasi.carezze.ui.theme.personColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
private val dateFormatter = DateTimeFormatter.ofPattern("d MMM").withZone(ZoneId.systemDefault())

@Composable
fun DashboardScreen(
    onNavigateToPersons: () -> Unit = {},
    onNavigateToHistory: (personId: String) -> Unit = {},
    onNavigateToAddTherapy: (personId: String) -> Unit = {},
    onNavigateToTherapyLog: (personId: String, therapyId: String) -> Unit = { _, _ -> },
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val persons by viewModel.persons.collectAsState()
    val filteredPersons by viewModel.filteredPersons.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val personColors by viewModel.personColors.collectAsState()
    var quickLogPersonId by remember { mutableStateOf<String?>(null) }

    DashboardContent(
        persons = persons,
        filteredPersons = filteredPersons,
        recentLogs = recentLogs,
        selectedPersonId = selectedPersonId,
        searchQuery = searchQuery,
        viewMode = viewMode,
        personColors = personColors,
        onSelectPerson = viewModel::selectPerson,
        onSearchQueryChange = viewModel::setSearchQuery,
        onToggleViewMode = viewModel::toggleViewMode,
        onNavigateToPersons = onNavigateToPersons,
        onNavigateToHistory = onNavigateToHistory,
        onOpenQuickLog = { personId -> quickLogPersonId = personId },
    )

    quickLogPersonId?.let { pid ->
        val personName = persons.find { it.id == pid }?.let { it.nickname ?: it.name } ?: ""
        QuickLogSheet(
            personId = pid,
            personName = personName,
            onDismiss = { quickLogPersonId = null },
            onNavigateToAddTherapy = { personId ->
                quickLogPersonId = null
                onNavigateToAddTherapy(personId)
            },
            onNavigateToTherapyLog = { personId, therapyId ->
                quickLogPersonId = null
                onNavigateToTherapyLog(personId, therapyId)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardContent(
    persons: List<Person>,
    filteredPersons: List<Person>,
    recentLogs: List<ActivityLog>,
    selectedPersonId: String?,
    searchQuery: String,
    viewMode: DashboardViewMode,
    personColors: Map<String, Int>,
    onSelectPerson: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleViewMode: () -> Unit,
    onNavigateToPersons: () -> Unit,
    onNavigateToHistory: (personId: String) -> Unit,
    onOpenQuickLog: (personId: String) -> Unit,
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
                },
            )
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
                personColors = personColors,
                onSelectPerson = onSelectPerson,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            PersonSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(4.dp))
            if (viewMode == DashboardViewMode.CARD) {
                CardView(
                    persons = filteredPersons,
                    allPersonsCount = persons.size,
                    recentLogs = recentLogs,
                    personColors = personColors,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToPersons = onNavigateToPersons,
                    onOpenQuickLog = onOpenQuickLog,
                )
            } else {
                FeedView(
                    logs = recentLogs,
                    persons = filteredPersons,
                    personColors = personColors,
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
    personColors: Map<String, Int>,
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
            val colorIndex = personColors[person.id] ?: 0
            FilterChip(
                selected = selectedPersonId == person.id,
                onClick = { onSelectPerson(person.id) },
                label = { Text(person.nickname ?: person.name) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(personColor(colorIndex), CircleShape),
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cerca per nome…", style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancella ricerca")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun CardView(
    persons: List<Person>,
    allPersonsCount: Int,
    recentLogs: List<ActivityLog>,
    personColors: Map<String, Int>,
    onNavigateToHistory: (personId: String) -> Unit,
    onNavigateToPersons: () -> Unit,
    onOpenQuickLog: (personId: String) -> Unit,
) {
    when {
        allPersonsCount == 0 -> {
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
        }
        persons.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Nessun risultato",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        else -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(persons) { person ->
                    val logCount = recentLogs.count { it.personId == person.id }
                    PersonCard(
                        person = person,
                        recentLogCount = logCount,
                        colorIndex = personColors[person.id] ?: 0,
                        onNavigateToHistory = { onNavigateToHistory(person.id) },
                        onOpenQuickLog = { onOpenQuickLog(person.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonCard(
    person: Person,
    recentLogCount: Int,
    colorIndex: Int,
    onNavigateToHistory: () -> Unit,
    onOpenQuickLog: () -> Unit,
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(personColor(colorIndex), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White,
                    )
                }
            }
            Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
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
            IconButton(onClick = onOpenQuickLog) {
                Icon(Icons.Default.Add, contentDescription = "Registra evento")
            }
        }
    }
}

@Composable
private fun FeedView(
    logs: List<ActivityLog>,
    persons: List<Person>,
    personColors: Map<String, Int>,
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
                    personColorIndex = personColors[log.personId] ?: 0,
                )
            }
        }
    }
}

@Composable
private fun ActivityLogFeedItem(
    log: ActivityLog,
    personName: String?,
    personColorIndex: Int,
) {
    ListItem(
        headlineContent = { Text(log.label()) },
        supportingContent = {
            if (personName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(personColor(personColorIndex), CircleShape),
                    )
                    Spacer(Modifier.size(4.dp))
                    Text("$personName · ${timeFormatter.format(log.timestamp)} ${dateFormatter.format(log.timestamp)}")
                }
            } else {
                Text("${timeFormatter.format(log.timestamp)} ${dateFormatter.format(log.timestamp)}")
            }
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
    val persons =
        listOf(
            Person("p1", "Francesco", "Io", "uid1", emptyMap()),
            Person("p2", "Jessica", "Amore", "uid1", emptyMap()),
            Person("p3", "Christian", "Chri", "uid1", emptyMap()),
            Person("p4", "Federico", "Fede", "uid1", emptyMap()),
            Person("p5", "Vittoria", "Vicky", "uid1", emptyMap()),
            Person("p6", "Antonina", "Nonna Nina", "uid1", emptyMap()),
            Person("p7", "Antonio", "Nonno Totò", "uid1", emptyMap()),
        )
    val log = ActivityLog.Diaper("l1", "p5", Instant.now(), "uid1", DiaperType.WET, null)
    CarezzeTheme {
        DashboardContent(
            persons = persons,
            filteredPersons = persons,
            recentLogs = listOf(log),
            selectedPersonId = null,
            searchQuery = "",
            viewMode = DashboardViewMode.CARD,
            personColors = mapOf("p1" to 0, "p2" to 2, "p3" to 4, "p5" to 3),
            onSelectPerson = {},
            onSearchQueryChange = {},
            onToggleViewMode = {},
            onNavigateToPersons = {},
            onNavigateToHistory = {},
            onOpenQuickLog = { _ -> },
        )
    }
}
