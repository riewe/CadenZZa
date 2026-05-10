package by.riewe.cadence.presentation.screens.trailer_change

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import by.riewe.cadence.R
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.DatePickerDialogField
import by.riewe.cadence.presentation.common.components.LithuanianPlate
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.data.local.entities.TrailerChangeEntity
import by.riewe.cadence.presentation.viewmodel.TrailerChangeViewModel
import by.riewe.cadence.utils.NumberSpaceTransformation
import by.riewe.cadence.utils.TrailerNumberTransformation
import by.riewe.cadence.utils.TruckNumberTransformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTrailerChangeScreen(
    cadenceId: Long? = null,
    changeId: Long? = null,
    viewModel: TrailerChangeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val activeChange by viewModel.activeChange.collectAsState()
    val selectedChange by viewModel.selectedChange.collectAsState()
    val error = viewModel.error
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(error) {
        error?.let {
            errorDialogMessage = it
            showErrorDialog = true
            viewModel.clearError()
        }
    }

    LaunchedEffect(cadenceId, changeId) {
        if (cadenceId != null) {
            viewModel.loadActiveChange(cadenceId)
        }
        if (changeId != null) {
            viewModel.loadChange(changeId)
        }
    }

    val isEditMode = changeId != null
    val currentChange = if (isEditMode) selectedChange else activeChange

    AddTrailerChangeContent(
        activeTrailerNumber = currentChange?.trailerNumber ?: "---",
        activeChange = if (isEditMode) null else currentChange, // Для валидации даты при добавлении нового
        initialData = if (isEditMode) selectedChange else null,
        isEditMode = isEditMode,
        onBack = onBack,
    ) { date, location, oldFuel, oldHours, donorTruck, newTrailer, newFuel, newHours ->
        if (isEditMode && changeId != null) {
            viewModel.updateTrailerChange(
                changeId = changeId,
                date = date,
                location = location,
                trailerNumber = newTrailer,
                donorTruckNumber = donorTruck,
                trailerFuel = newFuel,
                engineHours = newHours,
                isActive = selectedChange?.isActive ?: true,
                onSuccess = onSuccess
            )
        } else if (cadenceId != null) {
            viewModel.performTrailerChange(
                cadenceId = cadenceId,
                date = date,
                location = location,
                oldTrailerFuel = oldFuel,
                oldTrailerHours = oldHours,
                donorTruckNumber = donorTruck,
                newTrailerNumber = newTrailer,
                newTrailerFuel = newFuel,
                newTrailerHours = newHours,
                onSuccess = onSuccess
            )
        }
    }

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
fun AddTrailerChangeContent(
    activeTrailerNumber: String,
    activeChange: TrailerChangeEntity?,
    initialData: TrailerChangeEntity? = null,
    isEditMode: Boolean = false,
    onBack: () -> Unit,
    onSave: (Long, String, Int, Int, String, String, Int, Int) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    var dateStr by remember(initialData) { 
        mutableStateOf(dateFormatter.format(initialData?.let { Date(it.startDate) } ?: Date())) 
    }
    var location by remember(initialData) { mutableStateOf(initialData?.startLocation ?: "LT") }

    // "Отдаем" state (не используется в режиме редактирования)
    var oldTrailerFuel by remember { mutableStateOf("") }
    var oldTrailerHours by remember { mutableStateOf("") }

    // "Принимаем" state (или редактируемые данные)
    var donorTruckField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.donorTruckNumber ?: "")) 
    }
    var newTrailerField by remember(initialData) { 
        mutableStateOf(TextFieldValue(initialData?.trailerNumber ?: "")) 
    }
    var newTrailerFuel by remember(initialData) { 
        mutableStateOf(initialData?.startTrailerFuel?.toString() ?: "") 
    }
    var newTrailerHours by remember(initialData) { 
        mutableStateOf(initialData?.startEngineHours?.toString() ?: "") 
    }

    val dateLong = try {
        dateFormatter.parse(dateStr)?.time ?: System.currentTimeMillis()
    } catch (_: Exception) {
        System.currentTimeMillis()
    }

    val isDateValid = activeChange == null || dateLong >= activeChange.startDate

    val isFormValid = donorTruckField.text.length >= 3 && 
                      newTrailerField.text.length >= 3 &&
                      oldTrailerFuel.isNotBlank() &&
                      oldTrailerHours.isNotBlank() &&
                      newTrailerFuel.isNotBlank() &&
                      newTrailerHours.isNotBlank() &&
                      isDateValid

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        focusedLabelColor = OrangePrimary,
        cursorColor = OrangePrimary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Замена прицепа", fontWeight = FontWeight.Bold) },
                modifier = Modifier.shadow(4.dp),
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Date & Location
            MainCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DatePickerDialogField(
                        date = dateStr,
                        onDateSelected = { dateStr = it },
                        label = "Дата перецепа",
                        isError = !isDateValid
                    )

                    CountrySelector(
                        selectedCountryCode = location,
                        onCountrySelected = { location = it },
                        label = "Страна перецепа"
                    )
                }
            }

            // Card 2: "Отдаем"
            MainCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Отдаем:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LithuanianPlate(number = activeTrailerNumber)
                    }

                    OutlinedTextField(
                        value = oldTrailerFuel,
                        onValueChange = { if (it.all { c -> c.isDigit() }) oldTrailerFuel = it },
                        label = { Text("Топливо в рефе (л)") },
                        leadingIcon = { Icon(Icons.Default.LocalGasStation, null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = NumberSpaceTransformation,
                        singleLine = true,
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = oldTrailerHours,
                        onValueChange = { if (it.all { c -> c.isDigit() }) oldTrailerHours = it },
                        label = { Text("Моточасы (ч)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = NumberSpaceTransformation,
                        singleLine = true,
                        colors = textFieldColors
                    )
                }
            }

            // Card 3: "Принимаем"
            MainCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Принимаем:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = donorTruckField,
                        onValueChange = {
                            val clean = it.text.filter { char -> char.isLetterOrDigit() }.uppercase()
                            if (clean.length <= 7) {
                                donorTruckField = it.copy(text = clean)
                            }
                        },
                        label = { Text("Номер тягача-донора *") },
                        placeholder = { Text("Кто отдал прицеп") },
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.truck),
                                null,
                                modifier = Modifier.size(24.dp),
                                tint = OrangePrimary
                            )
                        },
                        colors = textFieldColors,
                        visualTransformation = TruckNumberTransformation,
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newTrailerField,
                        onValueChange = {
                            val clean = it.text.filter { char -> char.isLetterOrDigit() }.uppercase()
                            if (clean.length <= 6) {
                                newTrailerField = it.copy(text = clean)
                            }
                        },
                        label = { Text("Номер нового прицепа *") },
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.trailer),
                                null,
                                modifier = Modifier.size(24.dp),
                                tint = OrangePrimary
                            )
                        },
                        colors = textFieldColors,
                        visualTransformation = TrailerNumberTransformation,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newTrailerFuel,
                        onValueChange = { if (it.all { c -> c.isDigit() }) newTrailerFuel = it },
                        label = { Text("Топливо в рефе (л)") },
                        leadingIcon = { Icon(Icons.Default.LocalGasStation, null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = NumberSpaceTransformation,
                        singleLine = true,
                        colors = textFieldColors
                    )

                    OutlinedTextField(
                        value = newTrailerHours,
                        onValueChange = { if (it.all { c -> c.isDigit() }) newTrailerHours = it },
                        label = { Text("Моточасы (ч)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = NumberSpaceTransformation,
                        singleLine = true,
                        colors = textFieldColors
                    )
                }
            }

            Button(
                onClick = {
                    onSave(
                        dateLong,
                        location,
                        oldTrailerFuel.toIntOrNull() ?: 0,
                        oldTrailerHours.toIntOrNull() ?: 0,
                        donorTruckField.text.uppercase(),
                        newTrailerField.text.uppercase(),
                        newTrailerFuel.toIntOrNull() ?: 0,
                        newTrailerHours.toIntOrNull() ?: 0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("СОХРАНИТЬ", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTrailerChangePreview() {
    CadenceTheme {
        AddTrailerChangeContent(
            activeTrailerNumber = "A123BC",
            activeChange = null,
            onBack = {},
            onSave = { _, _, _, _, _, _, _, _ -> }
        )
    }
}
