package by.riewe.cadence.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import by.riewe.cadence.presentation.viewmodel.TruckSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruckSettingsScreen(
    truckNumber: String,
    viewModel: TruckSettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    
    var baseConsumption by remember { mutableStateOf("") }
    var weightCoefficient by remember { mutableStateOf("") }

    LaunchedEffect(truckNumber) {
        viewModel.loadSettings(truckNumber)
    }

    LaunchedEffect(settings) {
        settings?.let {
            baseConsumption = it.baseConsumption.toString()
            weightCoefficient = it.weightCoefficient.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки тягача $truckNumber") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = baseConsumption,
                onValueChange = { baseConsumption = it },
                label = { Text("Норма расхода топлива (л/100км)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weightCoefficient,
                onValueChange = { weightCoefficient = it },
                label = { Text("Коэффициент веса (л/т на 100км)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val base = baseConsumption.toDoubleOrNull() ?: 21.0
                    val coeff = weightCoefficient.toDoubleOrNull() ?: 0.3
                    viewModel.updateSettings(truckNumber, base, coeff)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
            
            Text(
                text = "Формула расхода топлива с грузом: Норма + (Вес * Коэффициент)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
