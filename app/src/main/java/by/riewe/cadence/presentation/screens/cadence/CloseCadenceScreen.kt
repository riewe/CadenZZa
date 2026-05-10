package by.riewe.cadence.presentation.screens.cadence

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.DatePickerDialogField
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.common.components.TimePickerDialogField
import by.riewe.cadence.presentation.viewmodel.CadenceViewModel
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.utils.NumberSpaceTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран для закрытия текущей активной каденции.
 * Пользователь вводит финальные показатели одометра, топлива и моточасов.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseCadenceScreen(
    viewModel: CadenceViewModel,
    onNavigateBack: () -> Unit,
) {
    val activeCadence = viewModel.activeCadence
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadActiveCadence()
    }

    CloseCadenceContent(
        activeCadenceNumber = activeCadence?.cadenceNumber?.toString(),
        cadenceStartDate = activeCadence?.startDate,
        isLoading = isLoading,
        onNavigateBack = onNavigateBack,
    ) { data ->
        viewModel.closeCadence(data) { onNavigateBack() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseCadenceContent(
    activeCadenceNumber: String?,
    cadenceStartDate: Date?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onCloseClick: (Map<String, String>) -> Unit
) {
    if (activeCadenceNumber == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Закрытие каденции") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                if (isLoading) CircularProgressIndicator()
                else Text("Активная каденция не найдена")
            }
        }
        return
    }

    val scope = rememberCoroutineScope()
    val dateReq = remember { BringIntoViewRequester() }
    val indicatorsReq = remember { BringIntoViewRequester() }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        focusedLabelColor = OrangePrimary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    var endDate by remember { mutableStateOf(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())) }
    var endTime by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var endOdoField by remember { mutableStateOf(TextFieldValue("")) }
    var endTruckFuelField by remember { mutableStateOf(TextFieldValue("")) }
    var endRefFuelField by remember { mutableStateOf(TextFieldValue("")) }
    var endMHField by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCountry by remember { mutableStateOf("LT") }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val parsedEndDate = try { dateFormat.parse(endDate) } catch (_: Exception) { null }
    val isEndDateValid = parsedEndDate == null || cadenceStartDate == null || !parsedEndDate.before(cadenceStartDate)

    var showValidationErrors by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Закрыть каденцию №$activeCadenceNumber", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            // Блок даты и времени
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatePickerDialogField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(dateReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); dateReq.bringIntoView() } },
                            date = endDate,
                            onDateSelected = { endDate = it },
                            label = "Дата окончания *",
                            isError = (showValidationErrors && endDate.isBlank()) || !isEndDateValid
                        )
                        TimePickerDialogField(
                            modifier = Modifier.fillMaxWidth(),
                            time = endTime,
                            onTimeSelected = { endTime = it },
                            label = "Время окончания *",
                            isError = showValidationErrors && endTime.isBlank()
                        )
                        CountrySelector(
                            selectedCountryCode = selectedCountry,
                            onCountrySelected = { selectedCountry = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = "Страна окончания *"
                        )
                    }
                }
            }

            // Блок финальных показателей
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(indicatorsReq)
                                .onFocusChanged { if (it.isFocused) scope.launch { delay(300); indicatorsReq.bringIntoView() } },
                            value = endOdoField,
                            onValueChange = { endOdoField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Финальный одометр (км) *") },
                            leadingIcon = { Icon(Icons.Default.Speed,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && endOdoField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = endTruckFuelField,
                            onValueChange = { endTruckFuelField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Топливо тягача (л) *") },
                            leadingIcon = { Icon(Icons.Default.LocalGasStation,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && endTruckFuelField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = endRefFuelField,
                            onValueChange = { endRefFuelField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Топливо прицепа (л) *") },
                            leadingIcon = { Icon(Icons.Default.LocalGasStation,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && endRefFuelField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = endMHField,
                            onValueChange = { endMHField = it.copy(text = it.text.filter { c -> c.isDigit() }) },
                            label = { Text("Моточасы рефа *") },
                            leadingIcon = { Icon(Icons.Default.Timer,
                                null, tint = OrangePrimary) },
                            visualTransformation = NumberSpaceTransformation,
                            colors = textFieldColors,
                            isError = showValidationErrors && endMHField.text.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                        )
                    }
                }
            }

            // Кнопка подтверждения
            item {

                    Button(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        onClick = {
                            if (endDate.isBlank() || endTime.isBlank() || endOdoField.text.isBlank() ||
                                endTruckFuelField.text.isBlank() || endRefFuelField.text.isBlank() || endMHField.text.isBlank() ||
                                !isEndDateValid
                            ) {
                                showValidationErrors = true
                            } else {
                                val data = mapOf(
                                    "endDate" to endDate,
                                    "endTime" to endTime,
                                    "endOdo" to endOdoField.text,
                                    "endTruckFuel" to endTruckFuelField.text,
                                    "endRefFuel" to endRefFuelField.text,
                                    "endMH" to endMHField.text,
                                    "country" to selectedCountry
                                )
                                onCloseClick(data)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = !isLoading && isEndDateValid
                    ) {
                        if (isLoading) CircularProgressIndicator(
                            Modifier.size(24.dp),
                            color = Color.White
                        )
                        else Text("ЗАКРЫТЬ КАДЕНЦИЮ", fontWeight = FontWeight.Bold)
                    }

            }
        }
    }
}

@Preview(showBackground = true, name = "Close Cadence Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Close Cadence Dark Mode"
)
@Composable
fun CloseCadencePreview() {
    CadenceTheme {
        CloseCadenceContent(
            activeCadenceNumber = "5",
            cadenceStartDate = Date(),
            isLoading = false,
            onNavigateBack = {},
            onCloseClick = {}
        )
    }
}
