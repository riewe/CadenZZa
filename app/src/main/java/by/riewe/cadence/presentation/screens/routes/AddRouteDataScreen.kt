package by.riewe.cadence.presentation.screens.routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.riewe.cadence.R
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.common.components.RefrigeModeSelector
import by.riewe.cadence.presentation.common.components.RouteInputField
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.presentation.viewmodel.RouteViewModel
import by.riewe.cadence.ui.components.NumberVisualTransformation
import by.riewe.cadence.utils.TrailerNumberTransformation
import by.riewe.cadence.utils.formatDate
import by.riewe.cadence.utils.formatNumberWithSpaces
import java.util.Date
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouteDataScreen(
    cadenceId: Long = -1L,
    routeId: Long = -1L,
    onNavigateBack: () -> Unit,
    viewModel: RouteViewModel = hiltViewModel()
) {
    val isClosingMode = routeId > 0
    val existingRoute by viewModel.route.collectAsStateWithLifecycle()
    val error by remember { derivedStateOf { viewModel.error } }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(error) {
        error?.let {
            errorDialogMessage = it
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    // Состояния для старта
    var routeNumber by remember { mutableIntStateOf(0) }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var startCountry by remember { mutableStateOf("LT") }
    var startOdometer by remember { mutableStateOf("") }
    var startEngineHours by remember { mutableStateOf("") }
    var goodsDescription by remember { mutableStateOf("") }
    var goodsWeight by remember { mutableStateOf("") }
    var cmrNumber by remember { mutableStateOf("") }
    var refrigeMode by remember { mutableStateOf("Выключен") }
    var temperatureValue by remember { mutableStateOf("") }
    var trailerNumberField by remember { mutableStateOf(TextFieldValue("")) }

    // Состояния для завершения
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endCountry by remember { mutableStateOf("LT") }
    var endOdometer by remember { mutableStateOf("") }
    var endEngineHours by remember { mutableStateOf("") }

    val emptyCargoRu = stringResource(R.string.empty_cargo)

    LaunchedEffect(cadenceId, routeId) {
        if (isClosingMode) {
            viewModel.loadRoute(routeId)
        } else {
            routeNumber = viewModel.getNextRouteNumber(cadenceId)
            cmrNumber = "$routeNumber/1"
        }
    }

    LaunchedEffect(existingRoute) {
        existingRoute?.let {
            if (isClosingMode) {
                routeNumber = it.routeNumber
                startDate = it.startDate.time
                startCountry = it.startLocation
                startOdometer = it.startOdometer.toString()
                startEngineHours = it.startEngineHours.toString()
                goodsDescription = it.goodsDescription
                goodsWeight = it.weight?.toString()?.replace('.', ',') ?: ""
                cmrNumber = it.cmrNumber ?: ""
                refrigeMode = it.agregateMode
                temperatureValue = it.temperatureValue ?: ""
                trailerNumberField = TextFieldValue(it.trailerNumber ?: "")
                
                // Данные завершения
                endCountry = it.endLocation ?: it.startLocation
                if (endOdometer.isEmpty()) endOdometer = it.endOdometer?.toString() ?: it.startOdometer.toString()
                if (endEngineHours.isEmpty()) endEngineHours = it.endEngineHours?.toString() ?: it.startEngineHours.toString()
            }
        }
    }


        AddRouteDataContent(
            routeNumber = routeNumber,
            isEditMode = isClosingMode,
            startDate = startDate,
            startCountry = startCountry,
            startOdometer = startOdometer,
            startEngineHours = startEngineHours,
            goodsDescription = goodsDescription,
            goodsWeight = goodsWeight,
            cmrNumber = cmrNumber,
            refrigeMode = refrigeMode,
            temperatureValue = temperatureValue,
            trailerNumberField = trailerNumberField,
            endDate = endDate,
            endCountry = endCountry,
            endOdometer = endOdometer,
            endEngineHours = endEngineHours,
            onStartDateChange = { newValue -> startDate = newValue },
            onCountryChange = { newValue -> startCountry = newValue },
            onOdometerChange = { newValue -> if (newValue.all { it.isDigit() }) startOdometer = newValue },
            onEngineHoursChange = { newValue -> if (newValue.all { it.isDigit() }) startEngineHours = newValue },
            onGoodsChange = { newValue -> goodsDescription = newValue },
            onWeightChange = { newValue ->
                val formatted = newValue.replace('.', ',')
                if (formatted.isEmpty() || formatted.matches(Regex("""^\d*,?\d{0,3}$"""))) {
                    goodsWeight = formatted
                }
            },
            onCmrChange = { newValue -> cmrNumber = newValue },
            onRefrigeModeChange = { newValue -> 
                refrigeMode = newValue
                if (newValue == "Выключен") temperatureValue = ""
            },
            onTemperatureChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() || it == '-' || it == '+' || it == ' ' || it == '/' || it == ',' }
                val parts = filtered.split(Regex("[ /,]+")).filter { it.isNotEmpty() }
                if (parts.size <= 2) {
                    temperatureValue = filtered
                }
            },
            onTrailerNumberChange = { newValue ->
                val clean = newValue.text.filter { char -> char.isLetterOrDigit() }.uppercase()
                if (clean.length <= 6) {
                    trailerNumberField = newValue.copy(text = clean)
                }
            },
            onEndDateChange = { newValue -> endDate = newValue },
            onEndCountryChange = { newValue -> endCountry = newValue },
            onEndOdometerChange = { newValue -> if (newValue.all { it.isDigit() }) endOdometer = newValue },
            onEndEngineHoursChange = { newValue -> if (newValue.all { it.isDigit() }) endEngineHours = newValue },
            onBack = onNavigateBack,
            onSave = {
                val sOdo = startOdometer.toIntOrNull() ?: 0
                val eOdo = endOdometer.toIntOrNull() ?: 0
                val isFinished = eOdo > 0
                
                val isNoCargo = goodsDescription.trim().equals(emptyCargoRu, ignoreCase = true)
                val w = goodsWeight.replace(',', '.').toDoubleOrNull() ?: 0.0
                val consumption = if (isNoCargo) 21.0 else 21.0 + (0.3 * w)
                val dist = if (eOdo > sOdo) eOdo - sOdo else 0
                val burned = (consumption * dist) / 100.0

                viewModel.saveRoute(
                    routeId = if (isClosingMode) routeId else 0,
                    cadenceId = cadenceId,
                    startDate = Date(startDate),
                    startLocation = startCountry,
                    startOdometer = sOdo,
                    startEngineHours = startEngineHours.toIntOrNull() ?: 0,
                    goodsDescription = goodsDescription,
                    weight = goodsWeight.replace(',', '.').toDoubleOrNull(),
                    cmrNumber = cmrNumber,
                    refrigeMode = refrigeMode,
                    temperatureValue = temperatureValue,
                    trailerNumber = trailerNumberField.text.ifEmpty { null },
                    endDate = if (isFinished) Date(endDate) else null,
                    endLocation = if (isFinished) endCountry else null,
                    endOdometer = if (isFinished) eOdo else null,
                    endEngineHours = if (isFinished) endEngineHours.toIntOrNull() else null,
                    fuelBurned = if (isFinished) (burned * 10.0).roundToInt() / 10.0 else null,
                    fuelConsumption = if (isFinished) consumption else null,
                    onSuccess = onNavigateBack
                )
            }
        )

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Ошибка") },
                text = { Text(errorDialogMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouteDataContent(
    routeNumber: Int,
    isEditMode: Boolean = false,
    startDate: Long,
    startCountry: String,
    startOdometer: String,
    startEngineHours: String,
    goodsDescription: String,
    goodsWeight: String,
    cmrNumber: String,
    refrigeMode: String,
    temperatureValue: String,
    trailerNumberField: TextFieldValue,
    endDate: Long,
    endCountry: String,
    endOdometer: String,
    endEngineHours: String,
    onStartDateChange: (Long) -> Unit,
    onCountryChange: (String) -> Unit,
    onOdometerChange: (String) -> Unit,
    onEngineHoursChange: (String) -> Unit,
    onGoodsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onCmrChange: (String) -> Unit,
    onRefrigeModeChange: (String) -> Unit,
    onTemperatureChange: (String) -> Unit,
    onTrailerNumberChange: (TextFieldValue) -> Unit,
    onEndDateChange: (Long) -> Unit,
    onEndCountryChange: (String) -> Unit,
    onEndOdometerChange: (String) -> Unit,
    onEndEngineHoursChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val isFinishedInput = endOdometer.isNotBlank()

    val startOdometerInt = startOdometer.toIntOrNull() ?: 0
    val endOdometerInt = endOdometer.toIntOrNull() ?: 0
    val isEndOdometerValid = endOdometer.isBlank() || endOdometerInt > startOdometerInt
    val isEndDateValid = !isFinishedInput || endDate >= startDate


    val emptyCargoRu = stringResource(R.string.empty_cargo)
    
    val isEmptyCargo = goodsDescription.trim().lowercase().let {
        it == emptyCargoRu.lowercase()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isEditMode) "Редактирование рейса №$routeNumber"
                                  else if (routeNumber > 0) "Начало рейса №$routeNumber" 
                                  else "Создание рейса №1",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isFinishedInput) "Полные данные" else "Отправление",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = OrangePrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MainCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Данные отправления",
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Дата отправления
                    OutlinedTextField(
                        value = formatDate(Date(startDate)),
                        onValueChange = {},
                        label = { Text("Дата отправления") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = OrangePrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartDatePicker = true },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else OrangePrimary,
                            disabledLabelColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    CountrySelector(
                        selectedCountryCode = startCountry,
                        onCountrySelected = onCountryChange,
                        label = "Страна отправления"
                    )

                    RouteInputField(
                        value = startOdometer,
                        onValueChange = onOdometerChange,
                        label = "Одометр (км)",
                        icon = Icons.Default.Speed,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = NumberVisualTransformation()
                    )

                    RouteInputField(
                        value = startEngineHours,
                        onValueChange = onEngineHoursChange,
                        label = "Моточасы",
                        icon = Icons.Default.History,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = NumberVisualTransformation()
                    )
                }
            }



            MainCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Груз и прицеп",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    RouteInputField(
                        value = goodsDescription,
                        onValueChange = onGoodsChange,
                        label = "Описание груза",
                        icon = Icons.Default.Description,
                        placeholder = "Что везем?",
                        singleLine = true,
                        minLines = 1
                    )

                    if (!isEmptyCargo) {
                        RouteInputField(
                            value = goodsWeight,
                            onValueChange = onWeightChange,
                            label = "Вес груза (тонны)",
                            icon = Icons.Default.Scale,
                            placeholder = "0,000",
                            keyboardType = KeyboardType.Decimal
                        )

                        RouteInputField(
                            value = cmrNumber,
                            onValueChange = onCmrChange,
                            label = "Номер CMR",
                            icon = Icons.Default.Numbers,
                            placeholder = "например: 6/1, 6/2"
                        )
                    }

                    // Номер прицепа
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = trailerNumberField,
                        onValueChange = onTrailerNumberChange,
                        label = { Text("Номер прицепа") },
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.trailer),
                                null,
                                Modifier.size(24.dp),
                                tint = OrangePrimary
                            )
                        },
                        visualTransformation = TrailerNumberTransformation,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    // Выбор режима рефрижератора
                    RefrigeModeSelector(
                        selectedMode = refrigeMode,
                        onModeSelected = onRefrigeModeChange
                    )

                    // Поле температуры
                    if (refrigeMode != "Выключен") {
                        RouteInputField(
                            value = temperatureValue,
                            onValueChange = onTemperatureChange,
                            label = "Температура (°C)",
                            icon = Icons.Default.Thermostat,
                            placeholder = "например: -18 или -18 / +5",
                            keyboardType = KeyboardType.Text
                        )
                    }
                }
            }

            MainCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Данные прибытия (необязательно)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Дата прибытия
                    OutlinedTextField(
                        value = formatDate(Date(endDate)),
                        onValueChange = {},
                        label = { Text("Дата прибытия") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = OrangePrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndDatePicker = true },
                        readOnly = true,
                        enabled = false,
                        isError = !isEndDateValid,
                        supportingText = if (!isEndDateValid) {
                            { Text("Дата не может быть раньше отправления") }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else OrangePrimary,
                            disabledLabelColor = if (!isEndDateValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    CountrySelector(
                        selectedCountryCode = endCountry,
                        onCountrySelected = onEndCountryChange,
                        label = "Страна прибытия"
                    )

                    RouteInputField(
                        value = endOdometer,
                        onValueChange = onEndOdometerChange,
                        label = "Одометр по прибытию (км)",
                        icon = Icons.Default.Speed,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = NumberVisualTransformation(),
                        isError = !isEndOdometerValid,
                        supportingText = if (!isEndOdometerValid) "Должен быть больше начального ($startOdometerInt)" else null
                    )

                    RouteInputField(
                        value = endEngineHours,
                        onValueChange = onEndEngineHoursChange,
                        label = "Моточасы по прибытию",
                        icon = Icons.Default.History,
                        keyboardType = KeyboardType.Number,
                        visualTransformation = NumberVisualTransformation()
                    )
                }
            }

            if (isFinishedInput && isEndOdometerValid && endOdometerInt > startOdometerInt) {
                val w = goodsWeight.replace(',', '.').toDoubleOrNull() ?: 0.0
                val consumption = if (isEmptyCargo) 21.0 else 21.0 + (0.3 * w)
                val dist = endOdometerInt - startOdometerInt
                val burned = (consumption * dist) / 100.0
                val totalMH = (endEngineHours.toIntOrNull() ?: 0) - (startEngineHours.toIntOrNull() ?: 0)


                CalculatedData(dist, consumption, burned, totalMH)

            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = startOdometer.isNotBlank() && isEndOdometerValid && isEndDateValid
            ) {
                Text(if (isFinishedInput) "Сохранить рейс" else "Начать рейс")
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onStartDateChange(it) }
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onEndDateChange(it) }
                    showEndDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun CalculatedData(distance: Int, cons: Double, burn: Double, engineHours: Int){
    MainCard {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Расчетные данные за рейс",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Дистанция:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${formatNumberWithSpaces(distance.toString())} км.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Средний расход: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${"%.1f".format(cons).replace('.', ',')} л/100км.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Потрачено топлива: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${"%.1f".format(burn).replace('.', ',')} л.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Работа рефрижератора: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${formatNumberWithSpaces(engineHours.toString())} м/ч.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Preview(showBackground = true)@Composable
fun CalculatedDataPreview() {
    CadenceTheme {
        // Просто вызываем функцию с тестовыми данными
        CalculatedData(
            distance = 1250,
            cons = 24.5,
            burn = 306.3,
            engineHours = 45
        )
    }
}

//@Preview(device = Devices.TABLET, showSystemUi = true, name = "Tablet")
@Preview(showBackground = true,
    device = "spec:width=411dp,height=1600dp",name = "Создание рейса", showSystemUi = true,
    wallpaper = Wallpapers.NONE
)
@Composable
fun AddRouteDataScreenStartPreview() {
    CadenceTheme {
        AddRouteDataContent(
            routeNumber = 5,
            isEditMode = false,
            startDate = System.currentTimeMillis(),
            startCountry = "LT",
            startOdometer = "125000",
            startEngineHours = "4500",
            goodsDescription = "Electronics",
            goodsWeight = "12,123",
            cmrNumber = "5/1",
            refrigeMode = "Выключен",
            temperatureValue = "",
            trailerNumberField = TextFieldValue(""),
            endDate = System.currentTimeMillis(),
            endCountry = "DE",
            endOdometer = "",
            endEngineHours = "",
            onStartDateChange = {},
            onCountryChange = {},
            onOdometerChange = {},
            onEngineHoursChange = {},
            onGoodsChange = {},
            onWeightChange = {},
            onCmrChange = {},
            onRefrigeModeChange = {},
            onTemperatureChange = {},
            onTrailerNumberChange = {},
            onEndDateChange = {},
            onEndCountryChange = {},
            onEndOdometerChange = {},
            onEndEngineHoursChange = {},
            onBack = {},
            onSave = {}
        )
    }
}

