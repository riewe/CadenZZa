package by.riewe.cadence.presentation.screens.cadence

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.riewe.cadence.R
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.DatePickerDialogField
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.common.components.TimePickerDialogField
import by.riewe.cadence.presentation.common.components.ValidatedTextField
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.presentation.viewmodel.CadenceViewModel
import by.riewe.cadence.utils.NumberSpaceTransformation
import by.riewe.cadence.utils.TrailerNumberTransformation
import by.riewe.cadence.utils.TruckNumberTransformation
import by.riewe.cadence.utils.formatDriverInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Экран создания или редактирования каденции.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCadenceScreen(
    viewModel: CadenceViewModel,
    onNavigateBack: () -> Unit,
    cadenceId: Long? = null
) {
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    var initialData by remember { mutableStateOf<Map<String, String>?>(null) }

    LaunchedEffect(cadenceId) {
        if (cadenceId != null) {
            viewModel.getCadence(cadenceId).collect { cadence ->
                cadence?.let {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    
                    val calendar = Calendar.getInstance().apply {
                        val timeMillis = it.startTime
                        set(Calendar.HOUR_OF_DAY, (timeMillis / 3600000).toInt())
                        set(Calendar.MINUTE, ((timeMillis % 3600000) / 60000).toInt())
                    }

                    initialData = mapOf(
                        "cadenceNumber" to it.cadenceNumber.toString(),
                        "driver1" to it.driver1,
                        "driver2" to (it.driver2 ?: ""),
                        "truckNumber" to it.truckNumber,
                        "trailerNumber" to it.trailerNumber,
                        "startDate" to dateFormat.format(it.startDate),
                        "startTime" to timeFormat.format(calendar.time),
                        "startOdo" to it.initialOdometer.toString(),
                        "startTruckFuel" to it.initialTruckFuel.toString(),
                        "startRefFuel" to it.initialTrailerFuel.toString(),
                        "startMH" to it.initialEngineHours.toString()
                    )
                }
            }
        }
    }

    CreateCadenceContent(
        isLoading = isLoading,
        error = error,
        suggestedNumber = viewModel.suggestedNumber,
        initialData = initialData,
        isEditMode = cadenceId != null,
        onNavigateBack = onNavigateBack,
        onSaveClick = { data ->
            if (cadenceId != null) {
                viewModel.updateCadence(cadenceId, data) { onNavigateBack() }
            } else {
                viewModel.createCadence(data) { onNavigateBack() }
            }
        },
        onClearError = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCadenceContent(
    isLoading: Boolean,
    error: String?,
    suggestedNumber: String?,
    initialData: Map<String, String>? = null,
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    onSaveClick: (Map<String, String>) -> Unit,
    onClearError: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val title = if (isEditMode) "Изменение данных каденции" else "Новая каденция"
    val buttonText = if (isEditMode) "ИЗМЕНИТЬ ДАННЫЕ" else "ОТКРЫТЬ КАДЕНЦИЮ"

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        focusedLabelColor = OrangePrimary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    // Состояния полей
    var cadenceNumber by remember(suggestedNumber, initialData) { 
        mutableStateOf(initialData?.get("cadenceNumber") ?: suggestedNumber ?: "") 
    }
    var driver1 by remember(initialData) { mutableStateOf(initialData?.get("driver1") ?: "") }
    var driver2 by remember(initialData) { mutableStateOf(initialData?.get("driver2") ?: "") }
    var truckNumberField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("truckNumber") ?: "")) 
    }
    var trailerNumberField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("trailerNumber") ?: "")) 
    }
    var startDate by remember(initialData) { mutableStateOf(initialData?.get("startDate") ?: "") }
    var startTime by remember(initialData) { mutableStateOf(initialData?.get("startTime") ?: "") }
    var startOdoField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("startOdo") ?: "")) 
    }
    var startTruckFuelField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("startTruckFuel") ?: "")) 
    }
    var startRefFuelField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("startRefFuel") ?: "")) 
    }
    var startMHField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.get("startMH") ?: "")) 
    }
    var selectedCountry by remember { mutableStateOf("LT") }

    // Реквизиторы для прокрутки при фокусе
    val cadenceReq = remember { BringIntoViewRequester() }
    val dateReq = remember { BringIntoViewRequester() }
    val d1Req = remember { BringIntoViewRequester() }
    val truckReq = remember { BringIntoViewRequester() }
    val trailerReq = remember { BringIntoViewRequester() }

    var showValidationErrors by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }

    error?.let {
        LaunchedEffect(it) {
            errorDialogMessage = it
            showErrorDialog = true
            onClearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = OrangePrimary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Номер каденции
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(cadenceReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); cadenceReq.bringIntoView() } },
                            value = cadenceNumber,
                            onValueChange = { cadenceNumber = it.filter { c -> c.isDigit() } },
                            label = { Text("Номер каденции *") },
                            leadingIcon = { Icon(Icons.Default.Numbers,
                                null, tint = OrangePrimary) },
                            isError = showValidationErrors && cadenceNumber.isBlank(),
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                    }
                }
            }

            // 2. Дата и время начала
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatePickerDialogField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(dateReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); dateReq.bringIntoView() } },
                            date = startDate,
                            onDateSelected = { startDate = it },
                            label = "Дата начала *",
                            isError = showValidationErrors && startDate.isBlank()
                        )
                        TimePickerDialogField(
                            modifier = Modifier.fillMaxWidth(),
                            time = startTime,
                            onTimeSelected = { startTime = it },
                            label = "Время начала *",
                            isError = showValidationErrors && startTime.isBlank()
                        )
                        CountrySelector(
                            selectedCountryCode = selectedCountry,
                            onCountrySelected = { selectedCountry = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Страна начала *"
                        )
                    }
                }
            }

            // 3. Водители
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ValidatedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(d1Req)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); d1Req.bringIntoView() } },
                            value = driver1,
                            onValueChange = { driver1 = formatDriverInput(it) },
                            label = "Водитель 1 *",
                            leadingIcon = { Icon(Icons.Default.Person,
                                null, tint = OrangePrimary) },
                            isError = showValidationErrors && driver1.isBlank(),
                            errorMessage = "Введите имя",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = driver2,
                            onValueChange = { driver2 = formatDriverInput(it) },
                            label = { Text("Водитель 2") },
                            colors = textFieldColors,
                            leadingIcon = { Icon(Icons.Default.People,
                                null, tint = OrangePrimary) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }
            }

            // 4. Тягач и начальные данные
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(truckReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); truckReq.bringIntoView() } },
                            value = truckNumberField,
                            onValueChange = {
                                val clean = it.text.filter { char -> char.isLetterOrDigit() }.uppercase()
                                if (clean.length <= 7) {
                                    truckNumberField = it.copy(text = clean)
                                }
                            },
                            label = { Text("Номер тягача *") },
                            leadingIcon = { Icon(painterResource(R.drawable.truck),
                                null, Modifier.size(24.dp), tint = OrangePrimary) },
                            colors = textFieldColors,
                            visualTransformation = TruckNumberTransformation,
                            isError = showValidationErrors && truckNumberField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = startOdoField,
                            onValueChange = { startOdoField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Одометр (км) *") },
                            leadingIcon = { Icon(Icons.Default.Speed,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && startOdoField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = startTruckFuelField,
                            onValueChange = { startTruckFuelField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Топливо тягача (л) *") },
                            leadingIcon = { Icon(Icons.Default.LocalGasStation,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && startTruckFuelField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                    }
                }
            }

            // 5. Прицеп
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(trailerReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); trailerReq.bringIntoView() } },
                            value = trailerNumberField,
                            onValueChange = {
                                val clean = it.text.filter { char -> char.isLetterOrDigit() }.uppercase()
                                if (clean.length <= 6) {
                                    trailerNumberField = it.copy(text = clean)
                                }
                            },
                            label = { Text("Номер прицепа *") },
                            leadingIcon = { Icon(painterResource(R.drawable.trailer),
                                null, Modifier.size(24.dp), tint = OrangePrimary) },
                            colors = textFieldColors,
                            visualTransformation = TrailerNumberTransformation,
                            isError = showValidationErrors && trailerNumberField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = startRefFuelField,
                            onValueChange = { startRefFuelField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Топливо прицепа (л) *") },
                            leadingIcon = { Icon(Icons.Default.LocalGasStation,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && startRefFuelField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = startMHField,
                            onValueChange = { startMHField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Моточасы рефа *") },
                            leadingIcon = { Icon(Icons.Default.Timer,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && startMHField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                        )
                    }
                }
            }

            // Кнопка сохранения
            item {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        onClick = {
                            val errors = mutableListOf<String>()

                            // Теперь данные в полях уже "чистые" благодаря VisualTransformation
                            val truckNum = truckNumberField.text
                            val trailerNum = trailerNumberField.text

                            if (cadenceNumber.isBlank()) errors.add("Номер каденции")
                            if (startDate.isBlank()) errors.add("Дата начала")
                            if (startTime.isBlank()) errors.add("Время начала")
                            if (driver1.isBlank()) errors.add("Водитель 1")
                            if (truckNum.isBlank()) errors.add("Номер тягача")
                            if (startOdoField.text.isBlank()) errors.add("Одометр")
                            if (startTruckFuelField.text.isBlank()) errors.add("Топливо тягача")
                            if (trailerNum.isBlank()) errors.add("Номер прицепа")
                            if (startRefFuelField.text.isBlank()) errors.add("Топливо прицепа")
                            if (startMHField.text.isBlank()) errors.add("Моточасы рефа")

                            if (errors.isNotEmpty()) {
                                errorDialogMessage =
                                    "Заполните поля:\n" + errors.joinToString("\n") { "• $it" }
                                showErrorDialog = true
                                showValidationErrors = true
                            } else {
                                val data = mapOf(
                                    "cadenceNumber" to cadenceNumber,
                                    "driver1" to driver1,
                                    "driver2" to driver2,
                                    "truckNumber" to truckNum,
                                    "trailerNumber" to trailerNum,
                                    "startDate" to startDate,
                                    "startTime" to startTime,
                                    "startOdo" to startOdoField.text,
                                    "startTruckFuel" to startTruckFuelField.text,
                                    "startRefFuel" to startRefFuelField.text,
                                    "startMH" to startMHField.text,
                                    "country" to selectedCountry
                                )
                                onSaveClick(data)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(
                            Modifier.size(24.dp),
                            color = Color.White
                        )
                        else Text(buttonText, fontWeight = FontWeight.Bold)
                    }

            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    // Диалог ошибки
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Внимание") },
            text = { Text(errorDialogMessage) },
            confirmButton = { TextButton(onClick = { showErrorDialog = false }) { Text("ОК") } }
        )
    }
}

@Preview(showBackground = true, name = "Create Cadence Light Mode",
    device = "spec:width=411dp,height=1400dp,dpi=420")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Create Cadence Dark Mode",
    device = "spec:width=411dp,height=1400dp,dpi=420"
)
@Composable
fun CreateCadencePreview() {
    CadenceTheme {
        CreateCadenceContent(
            isLoading = false,
            error = null,
            suggestedNumber = "123",
            onNavigateBack = {},
            onSaveClick = {},
            onClearError = {}
        )
    }
}
