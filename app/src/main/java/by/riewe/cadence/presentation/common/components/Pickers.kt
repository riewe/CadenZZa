package by.riewe.cadence.presentation.common.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.riewe.cadence.presentation.theme.CadenceTheme
import by.riewe.cadence.presentation.theme.OrangePrimary
import java.util.Calendar

@Composable
fun TimePickerDialogField(
    time: String,
    onTimeSelected: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = OrangePrimary
                ) 
            },
            readOnly = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.error
            )
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { timePickerDialog.show() }
        )
    }
}

@Composable
fun DatePickerDialogField(
    date: String,
    onDateSelected: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = OrangePrimary
                ) 
            },
            readOnly = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Отключаем фокус, чтобы работал только клик по Box
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = OrangePrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.error
            )
        )
        // Прозрачный слой поверх для отлова кликов
        Box(
            Modifier
                .matchParentSize()
                .clickable { datePickerDialog.show() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerDialogFieldPreview() {
    CadenceTheme {
        TimePickerDialogField(
            time = "12:00",
            onTimeSelected = {},
            label = "Select Time",
            isError = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerDialogFieldErrorPreview() {
    CadenceTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TimePickerDialogField(
                time = "12:00",
                onTimeSelected = {},
                label = "Arrival Time",
                isError = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogFieldPreview() {
    CadenceTheme {
        DatePickerDialogField(
            date = "01.01.2023",
            onDateSelected = {},
            label = "Select Date",
            isError = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogFieldErrorPreview() {
    CadenceTheme {
        DatePickerDialogField(
            date = "01.01.2023",
            onDateSelected = {},
            label = "Arrival Date",
            isError = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}




