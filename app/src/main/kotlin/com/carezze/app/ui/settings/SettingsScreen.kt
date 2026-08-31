package com.fpculcasi.carezze.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.ui.theme.CarezzeTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val user by viewModel.settingsState.collectAsStateWithLifecycle()

    SettingsContent(
        language = user?.language ?: Language.IT,
        temperatureUnit = user?.temperatureUnit ?: TemperatureUnit.C,
        quietHoursStart = user?.quietHoursStart ?: "22:00",
        quietHoursEnd = user?.quietHoursEnd ?: "07:00",
        onLanguageChange = viewModel::setLanguage,
        onTemperatureUnitChange = viewModel::setTemperatureUnit,
        onQuietHoursStartChange = viewModel::setQuietHoursStart,
        onQuietHoursEndChange = viewModel::setQuietHoursEnd,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    language: Language,
    temperatureUnit: TemperatureUnit,
    quietHoursStart: String,
    quietHoursEnd: String,
    onLanguageChange: (Language) -> Unit,
    onTemperatureUnitChange: (TemperatureUnit) -> Unit,
    onQuietHoursStartChange: (String) -> Unit,
    onQuietHoursEndChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SettingDropdown(
                label = "Lingua",
                options = Language.entries.map { it.name },
                selected = language.name,
                onSelected = { onLanguageChange(Language.valueOf(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingDropdown(
                label = "Unità temperatura",
                options = TemperatureUnit.entries.map { it.name },
                selected = temperatureUnit.name,
                onSelected = { onTemperatureUnitChange(TemperatureUnit.valueOf(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingDropdown(
                    label = "Silenzio da",
                    options = quietHourOptions(),
                    selected = quietHoursStart,
                    onSelected = onQuietHoursStartChange,
                    modifier = Modifier.weight(1f),
                )
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = "—")
                SettingDropdown(
                    label = "a",
                    options = quietHourOptions(),
                    selected = quietHoursEnd,
                    onSelected = onQuietHoursEndChange,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun quietHourOptions(): List<String> = (0..23).flatMap { h ->
    listOf("${"$h".padStart(2, '0')}:00", "${"$h".padStart(2, '0')}:30")
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    CarezzeTheme {
        SettingsContent(
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "07:00",
            onLanguageChange = {},
            onTemperatureUnitChange = {},
            onQuietHoursStartChange = {},
            onQuietHoursEndChange = {},
            onNavigateBack = {},
        )
    }
}
