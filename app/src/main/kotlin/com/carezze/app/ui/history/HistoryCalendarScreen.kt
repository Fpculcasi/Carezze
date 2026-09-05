package com.fpculcasi.carezze.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fpculcasi.carezze.domain.model.ActivityLog
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCalendarScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val logs by viewModel.logs.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    val logsByDate =
        logs.groupBy { log ->
            log.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            MonthHeader(
                month = currentMonth,
                onPrevious = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDay = null
                },
                onNext = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDay = null
                },
            )
            Spacer(Modifier.height(8.dp))
            DayOfWeekHeader()
            Spacer(Modifier.height(4.dp))
            CalendarGrid(
                month = currentMonth,
                logsByDate = logsByDate,
                selectedDay = selectedDay,
                onDayClick = { day ->
                    selectedDay = if (selectedDay == day) null else day
                },
            )
            AnimatedVisibility(visible = selectedDay != null) {
                selectedDay?.let { day ->
                    DayDetail(day = day, logs = logsByDate[day] ?: emptyList())
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mese precedente")
        }
        Text(
            text = monthFormatter.format(month).replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mese successivo")
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    val days = listOf("Lu", "Ma", "Me", "Gi", "Ve", "Sa", "Do")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    logsByDate: Map<LocalDate, List<ActivityLog>>,
    selectedDay: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = month.atDay(1)
    // DayOfWeek: MONDAY=1..SUNDAY=7; we want offset 0-based (Mon=0)
    val startOffset = (firstDayOfMonth.dayOfWeek.value - 1)
    val daysInMonth = month.lengthOfMonth()
    val cells = List(startOffset) { null } + (1..daysInMonth).map { month.atDay(it) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = false,
    ) {
        items(cells) { date ->
            if (date == null) {
                Box(modifier = Modifier.aspectRatio(1f))
            } else {
                val hasLogs = logsByDate.containsKey(date)
                val isSelected = date == selectedDay
                val isToday = date == LocalDate.now()
                Box(
                    modifier =
                        Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                },
                            )
                            .clickable { onDayClick(date) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        )
                        if (hasLogs) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            },
                                        ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetail(
    day: LocalDate,
    logs: List<ActivityLog>,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.ITALIAN)
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = dateFormatter.format(day).replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            if (logs.isEmpty()) {
                Text("Nessun evento", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                logs.sortedByDescending { it.timestamp }.forEach { log ->
                    ListItem(
                        headlineContent = { Text(log.label()) },
                        supportingContent = { Text(timeFormatter.format(log.timestamp)) },
                        leadingContent = { Text(log.emoji(), style = MaterialTheme.typography.titleMedium) },
                    )
                }
            }
        }
    }
}

private fun ActivityLog.label(): String =
    when (this) {
        is ActivityLog.Meal -> "Pasto"
        is ActivityLog.Diaper -> "Pannolino"
        is ActivityLog.SleepStart -> "Inizio sonno"
        is ActivityLog.SleepEnd -> "Fine sonno"
        is ActivityLog.Temperature -> "Temperatura · $temperature°${unit.name}"
        is ActivityLog.Weight -> "Peso · $weight ${weightUnit.name.lowercase()}"
        is ActivityLog.Hygiene -> "Igiene"
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
