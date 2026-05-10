package by.riewe.cadence.presentation.screens.refuelings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import by.riewe.cadence.R
import by.riewe.cadence.presentation.common.components.CountrySelector
import by.riewe.cadence.presentation.common.components.DatePickerDialogField
import by.riewe.cadence.presentation.common.components.FuelCardPicker
import by.riewe.cadence.presentation.common.components.MainCard
import by.riewe.cadence.presentation.common.components.RouteInputField
import by.riewe.cadence.presentation.theme.AdBlueColor
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import by.riewe.cadence.presentation.viewmodel.RefuelViewModel
import by.riewe.cadence.utils.NumberSpaceTransformation
import by.riewe.cadence.utils.formatDate
import java.util.Date

@Composable
fun AddRefuelScreen(
    cadenceId: Long,
    viewModel: RefuelViewModel = hiltViewModel(),
    onBack: () -> Unit,
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

    AddRefuelContent(
        onBack = onBack,
        onSave = { date, location, truckFuel, adBlue, trailerFuel, cardName ->
            viewModel.addRefuel(
                cadenceId = cadenceId,
                date = date,
                location = location,
                truckFuel = truckFuel,
                adBlue = adBlue,
                trailerFuel = trailerFuel,
                cardName = cardName,
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
fun AddRefuelContent(
    onBack: () -> Unit,
    onSave: (Date, String, Int, Int, Int, String) -> Unit
) {
    var dateString by remember { mutableStateOf(formatDate(Date())) }
    var location by remember { mutableStateOf("PL") }
    var truckFuel by remember { mutableStateOf("") }
    var adBlue by remember { mutableStateOf("") }
    var trailerFuel by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

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
                title = { Text("Добавить заправку", fontWeight = FontWeight.Bold) },
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = OrangePrimary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        DatePickerDialogField(
                            date = dateString,
                            onDateSelected = { dateString = it },
                            label = "Дата заправки *",
                            isError = false
                        )

                        CountrySelector(
                            selectedCountryCode = location,
                            onCountrySelected = { location = it },
                            label = "Страна заправки *"
                        )
                    }
                }
            }

            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RouteInputField(
                            value = truckFuel,
                            onValueChange = { truckFuel = it.filter { c -> c.isDigit() } },
                            label = "Топливо тягач (л)",
                            painterIcon = painterResource(R.drawable.truck),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = NumberSpaceTransformation
                        )

                        RouteInputField(
                            value = adBlue,
                            onValueChange = { adBlue = it.filter { c -> c.isDigit() } },
                            label = "AdBlue (л)",
                            painterIcon = painterResource(R.drawable.adblue),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = NumberSpaceTransformation
                        )

                        RouteInputField(
                            value = trailerFuel,
                            onValueChange = { trailerFuel = it.filter { c -> c.isDigit() } },
                            label = "Топливо реф (л)",
                            painterIcon = painterResource(R.drawable.trailer),
                            keyboardType = KeyboardType.Number,
                            visualTransformation = NumberSpaceTransformation
                        )
                    }
                }
            }

            item {
                MainCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        FuelCardPicker(
                            selectedCard = cardName,
                            onCardSelected = { cardName = it },
                            label = "Топливная карта"
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        // Парсинг даты из строки dd.MM.yyyy
                        val parts = dateString.split(".")
                        val date = if (parts.size == 3) {
                            val cal = java.util.Calendar.getInstance()
                            cal.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                            cal.time
                        } else Date()

                        onSave(
                            date,
                            location,
                            truckFuel.toIntOrNull() ?: 0,
                            adBlue.toIntOrNull() ?: 0,
                            trailerFuel.toIntOrNull() ?: 0,
                            cardName
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("СОХРАНИТЬ", fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true, name = "Add Refuel Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Add Refuel Dark Mode"
)
@Composable
fun AddRefuelPreview() {
    CadenceTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AddRefuelContent(
                onBack = {},
                onSave = { _, _, _, _, _, _ -> }
            )
        }
    }
}
