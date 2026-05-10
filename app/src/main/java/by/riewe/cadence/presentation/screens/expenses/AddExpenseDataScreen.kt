package by.riewe.cadence.presentation.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.CurrencyPicker
import by.riewe.cadence.presentation.common.components.DatePickerDialogField
import by.riewe.cadence.presentation.common.components.ExpenseDescriptionPicker
import by.riewe.cadence.presentation.common.components.FuelCardPicker
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.presentation.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    cadenceId: Long,
    viewModel: ExpenseViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
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

    AddExpenseContent(
        onBackClick = onBackClick,
        onSaveClick = { date, location, cardName, amount, currency, description ->
            viewModel.addExpense(
                cadenceId = cadenceId,
                date = date,
                location = location,
                cardName = cardName,
                amount = amount,
                currency = currency,
                description = description,
                onSuccess = onSuccess
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
fun AddExpenseContent(
    onBackClick: () -> Unit,
    onSaveClick: (
        date: Long,
        location: String,
        cardName: String,
        amount: Double,
        currency: String,
        description: String
    ) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    
    // Состояния для полей ввода на основе ExpenseEntity
    var dateStr by remember { mutableStateOf(dateFormatter.format(Date())) }
    var location by remember { mutableStateOf("LT") }
    var cardName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("EUR") }
    var description by remember { mutableStateOf("") }

    // Простая валидация: сумма, место и карта обязательны
    val isFormValid = amount.toDoubleOrNull() != null && 
                     amount.toDouble() > 0 && 
                     location.isNotBlank() && 
                     cardName.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить расход", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = OrangePrimary)
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
            MainCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Дата расхода
                    DatePickerDialogField(
                        date = dateStr,
                        onDateSelected = { dateStr = it },
                        label = "Дата",
                        isError = false
                    )

                    // Выбор страны
                    CountrySelector(
                        selectedCountryCode = location,
                        onCountrySelected = { location = it },
                        label = "Страна расхода"
                    )

                    // Способ оплаты (Карта)
                    FuelCardPicker(
                        selectedCard = cardName,
                        onCardSelected = { cardName = it },
                        label = "Способ оплаты"
                    )

                    // Сумма (на всю ширину)
                    OutlinedTextField(
                        value = amount.replace('.', ','),
                        onValueChange = {
                            val formatted = it.replace(',', '.')
                            if (formatted.all { char -> char.isDigit() || char == '.' }) {
                                amount = formatted
                            }
                        },
                        label = { Text("Сумма") },
                        placeholder = { Text("0.00") },
                        leadingIcon = { Icon(Icons.Default.Payments, null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            focusedLabelColor = OrangePrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    // Валюта (на всю ширину)
                    CurrencyPicker(
                        selectedCurrency = currency,
                        onCurrencySelected = { currency = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = "Валюта"
                    )


                    // Описание
                    ExpenseDescriptionPicker(
                        selectedDescription = description,
                        onDescriptionSelected = { description = it },
                        label = "Описание расхода"
                    )
                }
            }

            Button(
                onClick = {
                    val dateLong = try {
                        dateFormatter.parse(dateStr)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                    onSaveClick(
                        dateLong,
                        location,
                        cardName,
                        amount.toDoubleOrNull() ?: 0.0,
                        currency,
                        description
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
fun AddExpenseScreenPreview() {
    CadenceTheme {
        AddExpenseContent(
            onBackClick = {},
            onSaveClick = { _, _, _, _, _, _ -> }
        )
    }
}
